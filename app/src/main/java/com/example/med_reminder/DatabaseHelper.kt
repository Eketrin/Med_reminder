package com.example.med_reminder

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// тут работаем с БД

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,
    null, DATABASE_VERSION) {
    //константы для бд и её версии
    companion object {
        private const val DATABASE_NAME = "reminders.db" // Имя базы данных
        private const val DATABASE_VERSION = 1 // Версия базы данных
        const val TABLE_NAME = "reminders" // Имя таблицы
        const val COLUMN_ID = "id" // Имя столбца для идентификатора
        const val COLUMN_TEXT = "text" // Имя столбца для текста напоминания
        const val COLUMN_DATE = "date" // Имя столбца для даты напоминания
        const val COLUMN_TIME = "time" // Имя столбца для времени напоминания
    }

    //при создании базы данных
    override fun onCreate(db: SQLiteDatabase?) {
        //создание таблицы
        val createTableQuery = "CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TEXT TEXT, $COLUMN_DATE TEXT, $COLUMN_TIME TEXT)"
        //делаем запрос
        db?.execSQL(createTableQuery)
    }

    //при обновлении версии бд
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //запрос для удаления существующей таблицы, если она есть
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        // Создаем новую таблицу
        onCreate(db)
    }
}

