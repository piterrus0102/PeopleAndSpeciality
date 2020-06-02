package ru.piterrus.peopleandspecialty.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.piterrus.peopleandspecialty.MainActivity.Companion.setOfPersonsFromDB
import ru.piterrus.peopleandspecialty.MainActivity.Companion.tempSpeciality
import ru.piterrus.peopleandspecialty.R
import ru.piterrus.peopleandspecialty.dataclasses.SpecialtyClass

class SpecialtyFragment: Fragment() {

    private lateinit var tableOfSpecialitites: TableLayout

    private var listOfSpecialitites = ArrayList<SpecialtyClass>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_specialty, null)
        tableOfSpecialitites = v.findViewById(R.id.tableOfSpecialitites)
        getSpecialitities()
        getTableOfSpecialitites()
        return v
    }

    fun getSpecialitities(){
        for(i in setOfPersonsFromDB){
            for(j in i.specialty){
                val specialty = SpecialtyClass(j.specialty_id, j.specialty_name)
                var add = true
                for(k in listOfSpecialitites){
                    if(k.specialty_id == j.specialty_id){
                        add = false
                    }
                }
                if(add == true) {
                    listOfSpecialitites.add(specialty)
                }
            }
        }
    }

    fun getTableOfSpecialitites(){
        for(i in listOfSpecialitites){
            var k = 0
            val tableRow = TableRow(activity)
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 5
            lp.bottomMargin = 5
            tableRow.layoutParams = lp
            tableRow.setPadding(5,5,5,5)

            val textOfMessage = TextView(activity)
            for(m in setOfPersonsFromDB){
                for(n in m.specialty){
                    if(i.specialty_id == n.specialty_id) {
                        k++
                    }
                }
            }
            textOfMessage.text = i.specialty_name + " (" + k + " людей...)"
            textOfMessage.textSize = 24F

            tableRow.addView(textOfMessage)
            tableRow.setOnClickListener{
                tempSpeciality = i
                val ft = activity?.supportFragmentManager?.beginTransaction()
                ft?.replace(R.id.peopleLayout, PersonsFragment())
                ft?.addToBackStack(null)
                ft?.commit()
            }
            tableOfSpecialitites.addView(tableRow)
        }
    }

}