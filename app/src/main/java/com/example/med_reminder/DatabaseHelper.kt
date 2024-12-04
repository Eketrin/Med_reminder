package com.example.med_reminder

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "reminders.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "reminders"
        const val COLUMN_ID = "id"
        const val COLUMN_TEXT = "text"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TEXT TEXT, $COLUMN_DATE TEXT, $COLUMN_TIME TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }
}