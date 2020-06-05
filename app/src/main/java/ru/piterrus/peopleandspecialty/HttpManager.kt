package ru.piterrus.peopleandspecialty

import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import ru.piterrus.peopleandspecialty.dataclasses.PersonClass
import ru.piterrus.peopleandspecialty.dataclasses.SpecialtyClass
import java.io.IOException
import java.net.URL

class HttpManager(context: Context) {

    private var context = context

    fun isNetworkAvailableAndConnected(): Boolean {
        val runtime = Runtime.getRuntime()
        try {

            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return (exitValue == 0)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }


    fun getPersons(){
        val r = 0
        val database = MyDatabaseHelper(context)
        var db = database.readableDatabase
        val url = URL("https://gitlab.65apps.com/65gb/static/raw/master/testTask.json")
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                if (!isNetworkAvailableAndConnected()) {
                        //context.
                }
                if (isNetworkAvailableAndConnected()) {
                    Toast.makeText(context,"YES", Toast.LENGTH_LONG).show()
                }
                return
            }

            override fun onResponse(call: Call?, response: Response) {
                val json = response.body()?.string()
                val jsonObj = JSONObject(json)
                for (i in 0..jsonObj.getJSONArray("response").length() - 1) {
                    val f_name = jsonObj.getJSONArray("response").getJSONObject(i).get("f_name").toString().toLowerCase().capitalize()
                    val l_name = jsonObj.getJSONArray("response").getJSONObject(i).get("l_name").toString().toLowerCase().capitalize()
                    var birthday = jsonObj.getJSONArray("response").getJSONObject(i).get("birthday").toString()
                    birthday = DateManager(birthday).convertDateToDMY()
                    val avatr_url = jsonObj.getJSONArray("response").getJSONObject(i).get("avatr_url").toString()
                    val arrayOfSpecialty = ArrayList<SpecialtyClass>()
                    for (j in 0..jsonObj.getJSONArray("response").getJSONObject(i).getJSONArray("specialty").length() - 1) {
                        val specialty_id = jsonObj.getJSONArray("response").getJSONObject(i).getJSONArray("specialty").getJSONObject(j).getInt("specialty_id")
                        val specialty_name = jsonObj.getJSONArray("response").getJSONObject(i).getJSONArray("specialty").getJSONObject(j).getString("name")
                        val specialty = SpecialtyClass(specialty_id, specialty_name)
                        arrayOfSpecialty.add(specialty)
                    }
                    var containPerson = false
                    val c = db.query("persons", null, null, null, null, null, null)
                    if (c.moveToFirst()) {
                        val f_nameDB = c.getColumnIndex("F_NAME")
                        val l_nameDB = c.getColumnIndex("L_NAME")
                        val birthdayDB = c.getColumnIndex("BIRTHDAY")
                        val avatr_urlDB = c.getColumnIndex("AVATR_URL")
                        do {
                            if (c.getString(f_nameDB) == f_name && c.getString(l_nameDB) == l_name && c.getString(birthdayDB) == birthday && c.getString(avatr_urlDB) == avatr_url) {
                                containPerson = true
                                break
                            }
                        } while (c.moveToNext())
                    }
                    c.close()
                    if (!containPerson) {
                        db = database.readableDatabase
                        val values = ContentValues().apply {
                            put(MyDatabaseHelper.Entry.PERSON_F_NAME, f_name)
                            put(MyDatabaseHelper.Entry.PERSON_L_NAME, l_name)
                            put(MyDatabaseHelper.Entry.PERSON_BIRTHDAY, birthday)
                            put(MyDatabaseHelper.Entry.PERSON_AVATR_URL, avatr_url)
                        }
                        val speciality = JSONArray()
                        for (j in arrayOfSpecialty.indices) {
                            val newJSONSpeciality = JSONObject().put("speciality_id", arrayOfSpecialty[j].specialty_id).put("speciality_name", arrayOfSpecialty[j].specialty_name)
                            speciality.put(j, newJSONSpeciality)
                        }
                        val n = speciality.toString()
                        values.apply {
                            put(MyDatabaseHelper.Entry.PERSON_SPECIALTY, n)
                        }
                        db?.insert(MyDatabaseHelper.Entry.TABLE_NAME, null, values)
                    }
                }
            }
        })
    }
}