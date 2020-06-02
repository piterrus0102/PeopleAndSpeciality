package ru.piterrus.peopleandspecialty

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class MyDatabaseHelper(context: Context): SQLiteOpenHelper(context, "LP_STORAGE", null, 1) {


    object Entry : BaseColumns {
        const val TABLE_NAME = "persons"
        const val PERSON_F_NAME = "F_NAME"
        const val PERSON_L_NAME = "L_NAME"
        const val PERSON_BIRTHDAY = "BIRTHDAY"
        const val PERSON_AVATR_URL = "AVATR_URL"
        const val PERSON_SPECIALTY = "SPECIALTY"
    }

    private val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
            Entry.TABLE_NAME + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Entry.PERSON_F_NAME + " TEXT, " +
            Entry.PERSON_L_NAME + " TEXT, " +
            Entry.PERSON_BIRTHDAY + " TEXT, " +
            Entry.PERSON_AVATR_URL + " TEXT, " +
            Entry.PERSON_SPECIALTY + " TEXT" + ")"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}