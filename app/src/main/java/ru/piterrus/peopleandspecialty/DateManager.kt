package ru.piterrus.peopleandspecialty

import java.text.ParseException
import java.text.SimpleDateFormat

class DateManager(date: String) {

    private var date: String = date

    fun convertDateToDMY() : String{
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

    fun convertFromDMYToAge(): Long{
        try {
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            val d1 = formatter.parse(date).time
            val d2 = System.currentTimeMillis()
            val d3 = (d2-d1)
            val d4 = 365*24*60*60
            val d5 = d3 / d4 / 1000
            return d5
        } catch (e: IllegalArgumentException){
            return 0
        } catch (e: ParseException){
            return 0
        }
    }

}