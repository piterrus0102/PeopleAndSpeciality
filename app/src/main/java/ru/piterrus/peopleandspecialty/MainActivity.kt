package ru.piterrus.peopleandspecialty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import org.json.JSONArray
import ru.piterrus.peopleandspecialty.dataclasses.PersonClass
import ru.piterrus.peopleandspecialty.dataclasses.SpecialtyClass
import ru.piterrus.peopleandspecialty.fragments.SpecialtyFragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var getDatabaseButton: Button
    private lateinit var dbHelper: MyDatabaseHelper

    companion object{
        var setOfPersonsFromDB = HashSet<PersonClass>()
        lateinit var tempSpeciality: SpecialtyClass
        lateinit var tempPerson: PersonClass
    }

    private lateinit var downloadButton: Button

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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.downloadButton -> {
                HttpManager(this).getPersons()
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
}
