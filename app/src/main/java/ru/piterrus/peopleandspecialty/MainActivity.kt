package ru.piterrus.peopleandspecialty

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import ru.piterrus.peopleandspecialty.dataclasses.PersonClass
import ru.piterrus.peopleandspecialty.dataclasses.SpecialtyClass
import ru.piterrus.peopleandspecialty.fragments.SpecialtyFragment
import java.io.IOException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var getDatabaseButton: Button
    lateinit var dbHelper: MyDatabaseHelper
    private var setOfPersons = HashSet<PersonClass>()

    var containPerson = false
    var databaseIsEmpty = true

    companion object{
        var setOfPersonsFromDB = HashSet<PersonClass>()
        lateinit var tempSpeciality: SpecialtyClass
        lateinit var tempPerson: PersonClass
    }

    lateinit var downloadButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        dbHelper = MyDatabaseHelper(this)

        downloadButton = findViewById(R.id.downloadButton)
        downloadButton.setOnClickListener(this)

        getDatabaseButton = findViewById(R.id.getDatabase)
        getDatabaseButton.setOnClickListener(this)
    }

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.downloadButton -> {
                val url = URL("https://gitlab.65apps.com/65gb/static/raw/master/testTask.json")
                val okHttpClient = OkHttpClient()
                val request: Request = Request.Builder().url(url).build()
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        if (!isNetworkAvailableAndConnected()) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Проверьте интернет-соединение",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return
                        }
                        if (isNetworkAvailableAndConnected()) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Нет связи с сервером",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        return
                    }

                    override fun onResponse(call: Call?, response: Response) {
                        val json = response.body()?.string()
                        val jsonObj = JSONObject(json)
                        for (i in 0..jsonObj.getJSONArray("response").length() - 1) {
                            var f_name =
                                jsonObj.getJSONArray("response").getJSONObject(i).get("f_name")
                                    .toString()
                            f_name = f_name.toLowerCase()
                            f_name = f_name.capitalize()
                            var l_name =
                                jsonObj.getJSONArray("response").getJSONObject(i).get("l_name")
                                    .toString()
                            l_name = l_name.toLowerCase()
                            l_name = l_name.capitalize()
                            var birthday = jsonObj.getJSONArray("response").getJSONObject(i)
                                .get("birthday").toString()
                            birthday = convertDate(birthday)
                            val avatr_url = jsonObj.getJSONArray("response").getJSONObject(i)
                                .get("avatr_url").toString()
                            val arrayOfSpecialty = ArrayList<SpecialtyClass>()
                            for (j in 0..jsonObj.getJSONArray("response").getJSONObject(i).getJSONArray("specialty").length() - 1) {
                                val specialty_id =
                                    jsonObj.getJSONArray("response").getJSONObject(i)
                                        .getJSONArray("specialty").getJSONObject(j)
                                        .getInt("specialty_id")
                                val specialty_name =
                                    jsonObj.getJSONArray("response").getJSONObject(i)
                                        .getJSONArray("specialty").getJSONObject(j)
                                        .getString("name")
                                val specialty =
                                    SpecialtyClass(
                                        specialty_id,
                                        specialty_name
                                    )
                                arrayOfSpecialty.add(specialty)
                            }
                            val person = PersonClass(f_name, l_name, birthday, avatr_url, arrayOfSpecialty)
                            setOfPersons.add(person)
                        }
                        for (i in setOfPersons) {
                            val db1 = dbHelper.readableDatabase
                            val c = db1.query("persons", null, null, null, null, null, null)
                            if (c.moveToFirst()) {
                                databaseIsEmpty = false
                                val f_name = c.getColumnIndex("F_NAME")
                                val l_name = c.getColumnIndex("L_NAME")
                                val birthday = c.getColumnIndex("BIRTHDAY")
                                val avatr_url = c.getColumnIndex("AVATR_URL")
                                do {
                                    if (c.getString(f_name) == i.f_name && c.getString(l_name) == i.l_name && c.getString(
                                            birthday
                                        ) == i.birthday && c.getString(avatr_url) == i.avatr_url
                                    ) {
                                        containPerson = true
                                        break
                                    }
                                } while (c.moveToNext())
                            } else {
                                databaseIsEmpty = true
                            }
                            c.close()
                            if (!containPerson) {
                                val db = dbHelper.writableDatabase
                                val values = ContentValues().apply {
                                    put(MyDatabaseHelper.Entry.PERSON_F_NAME, i.f_name)
                                    put(MyDatabaseHelper.Entry.PERSON_L_NAME, i.l_name)
                                    put(MyDatabaseHelper.Entry.PERSON_BIRTHDAY, i.birthday)
                                    put(MyDatabaseHelper.Entry.PERSON_AVATR_URL, i.avatr_url)
                                }
                                val speciality = JSONArray()
                                for (j in 0..i.specialty.size - 1) {
                                    val newJSONSpeciality =
                                        JSONObject().put(
                                            "speciality_id",
                                            i.specialty[j].specialty_id
                                        )
                                            .put(
                                                "speciality_name",
                                                i.specialty[j].specialty_name
                                            )
                                    speciality.put(j, newJSONSpeciality)
                                }
                                val n = speciality.toString()
                                values.apply {
                                    put(MyDatabaseHelper.Entry.PERSON_SPECIALTY, n)
                                }
                                db?.insert(MyDatabaseHelper.Entry.TABLE_NAME, null, values)
                            }
                            else{
                                containPerson = false
                            }
                        }
                    }

                })
            }
            R.id.getDatabase -> {
                setOfPersonsFromDB.clear()
                val db = dbHelper.readableDatabase
                val c = db.query("persons", null, null, null, null, null, null)

                if (c.moveToFirst()) {
                    val f_name = c.getColumnIndex("F_NAME")
                    val l_name = c.getColumnIndex("L_NAME")
                    val birthday = c.getColumnIndex("BIRTHDAY")
                    val avatr_url = c.getColumnIndex("AVATR_URL")
                    val specialty = c.getColumnIndex("SPECIALTY")

                    do {
                        val list = ArrayList<SpecialtyClass>()
                        val jsonArray = JSONArray(c.getString(specialty))
                        for (i in 0..jsonArray.length() - 1){
                            val id = jsonArray.getJSONObject(i).getInt("speciality_id")
                            val name = jsonArray.getJSONObject(i).getString("speciality_name")
                            val speciality = SpecialtyClass(id, name)
                            list.add(speciality)
                        }
                        val person = PersonClass(c.getString(f_name), c.getString(l_name), c.getString(birthday), c.getString(avatr_url), list)
                        setOfPersonsFromDB.add(person)

                    } while (c.moveToNext())
                }
                c.close()
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.specialtyLayout, SpecialtyFragment())
                ft.addToBackStack(null)
                ft.commit()
            }
        }
    }

    fun convertDate(date: String) : String{
        try {
            val df: SimpleDateFormat
            val q = date.split("-")
            val n = q[0].toInt()
            df = if(n / 100 == 0){
                SimpleDateFormat("dd-MM-yyyy")
            }
            else {
                SimpleDateFormat("yyyy-MM-dd")
            }
            val date1 = df.parse(date)
            df.applyPattern("dd.MM.yyyy")
            var result = df.format(date1)
            return result
        } catch (e: IllegalArgumentException){
            return "<< <<"
        } catch (e: ParseException){
            return "<< <<"
        }
    }
}
