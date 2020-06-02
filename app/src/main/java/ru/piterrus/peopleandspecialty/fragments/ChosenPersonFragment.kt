package ru.piterrus.peopleandspecialty.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.piterrus.peopleandspecialty.MainActivity.Companion.tempPerson
import ru.piterrus.peopleandspecialty.R
import java.text.ParseException
import java.text.SimpleDateFormat

class ChosenPersonFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_chosen_person, null)
        val personName = v.findViewById<TextView>(R.id.personName)
        val dateOfBirth = v.findViewById<TextView>(R.id.dateOfBirth)
        val personAge = v.findViewById<TextView>(R.id.personAge)
        val personSpeciality = v.findViewById<TextView>(R.id.personSpeciality)

        personName.text = "Фамилия, Имя: ${tempPerson.l_name} ${tempPerson.f_name}"
        if(tempPerson.birthday == "<< <<") {
            dateOfBirth.text = "Дата рождения: -"
        }
        else{
            dateOfBirth.text = "Дата рождения: ${tempPerson.birthday} г."
        }
        val convertToAge = convertToAge(tempPerson.birthday!!)
        val ageToInt = convertToAge.toInt()
        if(ageToInt != 0) {
            if(ageToInt % 10 in 5..9 || ageToInt % 10 == 0) {
                personAge.text = "Возраст: $convertToAge лет"
            }
            else if(ageToInt % 10 in 2..4){
                personAge.text = "Возраст: $convertToAge года"
            }
            else{
                personAge.text = "Возраст: $convertToAge год"
            }
        }
        else{
            personAge.text = "Возраст: возраст неизвестен"
        }
        var resultString = "Специальность: "
        for(i in tempPerson.specialty){
            if(tempPerson.specialty.last() != i) {
                resultString += i.specialty_name + ", "
            }
            else{
                resultString += i.specialty_name
            }
        }
        personSpeciality.text = resultString
        return v
    }

    fun convertToAge(date: String): Long{
        try {
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            val d1 = formatter.parse(date).time
            val d2 = System.currentTimeMillis()
            val d3 = (d2-d1)
            val d4: Int = 365*24*60*60
            val d5 = d3 / d4 / 1000
            return d5
        } catch (e: IllegalArgumentException){
            return 0
        } catch (e: ParseException){
            return 0
        }
    }
}