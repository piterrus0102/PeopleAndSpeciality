package ru.piterrus.peopleandspecialty.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.piterrus.peopleandspecialty.DateManager
import ru.piterrus.peopleandspecialty.MainActivity.Companion.setOfPersonsFromDB
import ru.piterrus.peopleandspecialty.MainActivity.Companion.tempPerson
import ru.piterrus.peopleandspecialty.MainActivity.Companion.tempSpeciality
import ru.piterrus.peopleandspecialty.R
import java.text.ParseException
import java.text.SimpleDateFormat

class PersonsFragment: Fragment() {


    lateinit var tableOfPersons: TableLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_persons, null)
        tableOfPersons = v.findViewById(R.id.tableOfPersons)

        getPersons()
        return v

    }

    fun getPersons(){
        for (i in setOfPersonsFromDB){
            for (j in i.specialty){
                if(j.specialty_id == tempSpeciality.specialty_id){
                    val tableRow = TableRow(activity)
                    val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)
                    lp.topMargin = 5
                    lp.bottomMargin = 5
                    tableRow.layoutParams = lp
                    tableRow.setPadding(5,5,5,5)

                    val textOfMessage = TextView(activity)
                    val convertToAge = DateManager(i.birthday!!).convertFromDMYToAge()
                    val ageToInt = convertToAge.toInt()
                    if(ageToInt != 0) {
                        if(ageToInt % 10 in 5..9 || ageToInt % 10 == 0) {
                            textOfMessage.text =
                                i.l_name + " " + i.f_name + ", " + convertToAge + " лет"
                        }
                        else if(ageToInt % 10 in 2..4){
                            textOfMessage.text =
                                i.l_name + " " + i.f_name + ", " + convertToAge + " года"
                        }
                        else{
                            textOfMessage.text =
                                i.l_name + " " + i.f_name + ", " + convertToAge + " год"
                        }
                    }
                    else{
                        textOfMessage.text = i.l_name + " " + i.f_name + ", возраст неизвестен"
                    }
                    textOfMessage.textSize = 18F

                    tableRow.addView(textOfMessage)

                    tableRow.setOnClickListener{
                        tempPerson = i
                        val ft = activity?.supportFragmentManager?.beginTransaction()
                        ft?.replace(R.id.peopleLayout, ChosenPersonFragment())
                        ft?.addToBackStack(null)
                        ft?.commit()
                    }

                    tableOfPersons.addView(tableRow)
                }
            }
        }
    }
}