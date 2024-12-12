package com.example.med_reminder

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// тут работаем с БД

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // Константы для БД и её версии
    companion object {
        private const val DATABASE_NAME = "reminders.db" // Имя базы данных
        private const val DATABASE_VERSION = 2 // Увеличиваем версию базы данных
        const val TABLE_NAME = "reminders" // Имя таблицы
        const val COLUMN_ID = "id" // Имя столбца для идентификатора
        const val COLUMN_TEXT = "text" // для текста напоминания
        const val COLUMN_DATE = "date" // для даты напоминания
        const val COLUMN_TIME = "time" // для времени напоминания
        const val COLUMN_TAKED = "taked" //для выпито/не выпито
        const val COLUMN_DOSE = "dose" // для дозы 1,25
        const val COLUMN_PIECE = "piece" // для текста единицы измерения
    }

    // При создании базы данных
    override fun onCreate(db: SQLiteDatabase?) {
        // Создание таблицы с новыми столбцами
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_TEXT TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_TAKED BOOLEAN DEFAULT 0,  
                $COLUMN_DOSE TEXT DEFAULT 0.0,     
                $COLUMN_PIECE TEXT DEFAULT 'pill'     
            )
        """.trimIndent()
        // Делаем запрос
        db?.execSQL(createTableQuery)
    }

    // При обновлении версии БД
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Запрос для удаления существующей таблицы, если она есть
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        // Создаем новую таблицу
        onCreate(db)
    }
}


