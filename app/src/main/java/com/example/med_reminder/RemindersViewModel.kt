package com.example.med_reminder

import android.app.AlarmManager
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale

// тут описана логика напоминаний

class RemindersViewModel : ViewModel() {
    // Объект для работы с базой данных
    lateinit var dbHelper: DatabaseHelper
    // Объект для управления будильниками
    lateinit var alarmManager: AlarmManager

    // Состояния для текста, даты и времени напоминания
    var text by mutableStateOf("") // Текст напоминания
    var date by mutableStateOf("") // Дата напоминания
    var time by mutableStateOf("") // Время напоминания
    var taked by mutableStateOf(false) // Статус выпито/не выпито
    var dose by mutableStateOf("") // Доза
    var piece by mutableStateOf("") // Единица измерения

    // Список напоминаний, который будет изменяемым состоянием
    var reminders = mutableStateListOf<Reminder>()
        private set // Ограничиваем доступ к изменению списка извне

    fun addReminder(context: Context) {
        // Проверяем, заполнены ли дата и время
        if (date.isEmpty() && time.isEmpty()) {
            return Toast.makeText(context, R.string.toast_datetime_error, Toast.LENGTH_LONG).show()
        } else if (text.isEmpty()) {
            return Toast.makeText(context, R.string.toast_text_error, Toast.LENGTH_LONG).show()
        }

        if (date.isEmpty()) date = Utils.getCurrentDate()
        if (time.isEmpty()) time = "12:00"

        // Создаем новое напоминание
        val reminder = Reminder(Utils.getID(), text, date, time, taked, dose, piece)
        reminders.add(reminder) // Добавляем его в список напоминаний

        // Сохраняем напоминание в базе данных
        dbHelper.writableDatabase?.insert(DatabaseHelper.TABLE_NAME, null, ContentValues().apply {
            put(DatabaseHelper.COLUMN_ID, reminder.id)
            put(DatabaseHelper.COLUMN_TEXT, reminder.text)
            put(DatabaseHelper.COLUMN_DATE, reminder.date)
            put(DatabaseHelper.COLUMN_TIME, reminder.time)
            put(DatabaseHelper.COLUMN_TAKED, reminder.taked)
            put(DatabaseHelper.COLUMN_DOSE, reminder.dose)
            put(DatabaseHelper.COLUMN_PIECE, reminder.piece)
        })

        // Очищаем поля ввода после добавления напоминания
        text = ""
        date = ""
        time = ""
        taked = false
        dose = ""
        piece = ""

        // Планируем уведомление для напоминания
        scheduleNotification(context, reminder.date, reminder.time, reminder.text, reminder.id)
        // Сортируем список напоминаний
        sortReminders()
        // Показываем сообщение о том, что напоминание было создано
        Toast.makeText(context, R.string.toast_reminder_created, Toast.LENGTH_LONG).show()
    }

    // Функция для удаления напоминания
    fun removeReminder(reminder: Reminder, context: Context) {
        reminders.remove(reminder) // Удаляем напоминание из списка
        // Удаляем напоминание из базы данных
        dbHelper.writableDatabase?.delete(DatabaseHelper.TABLE_NAME, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(reminder.id.toString()))
        // Отменяем запланированное уведомление
        alarmManager.cancel(Utils.getPendingIntent(context, reminder.id, reminder.text))
        // Показываем сообщение о том, что напоминание было удалено
        Toast.makeText(context, R.string.toast_reminder_removed, Toast.LENGTH_LONG).show()
    }

    // Функция для получения всех напоминаний из базы данных
    fun getReminders(context: Context) {
        reminders.clear() // Очищаем текущий список напоминаний
        // Запрашиваем данные из базы данных
        val cursor = dbHelper.readableDatabase?.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
        // Проверяем, есть ли данные в курсоре
        if (cursor?.moveToFirst() == true) {
            do {
                // Извлекаем данные напоминания из курсора
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT))
                val date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME))
                val taked = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TAKED)) == 1 // Преобразуем в Boolean
                val dose = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DOSE))
                val piece = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PIECE))

                // Создаем объект напоминания
                val reminder = Reminder(id, text, date, time, taked, dose, piece)
                // Проверяем, не истекло ли время напоминания
                if (Utils.isReminderInPast(date, time)) {
                    removeReminder(reminder, context) // Удаляем напоминание, если оно истекло
                } else {
                    reminders.add(reminder) // Добавляем напоминание в список
                }
            } while (cursor.moveToNext()) // Переходим к следующему элементу в курсоре
        }
        cursor?.close() // Закрываем курсор
        sortReminders() // Сортируем список напоминаний
    }

    // Функция для сортировки напоминаний по дате и времени
    fun sortReminders() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") // Формат даты и времени
        reminders.sortWith(compareBy { reminder ->
            LocalDateTime.parse("${reminder.date} ${reminder.time}", formatter) // Сортируем напоминания
        })
    }

    // Функция для планирования уведомления
    fun scheduleNotification(context: Context, date: String, time: String, text: String, id: Int) {
        val dateTime = "$date $time" // Объединяем дату и время
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) // Формат даты и времени
        val triggerTime = sdf.parse(dateTime)?.time ?: return // Получаем время срабатывания уведомления
        val pendingIntent = Utils.getPendingIntent(context, id, text) // Получаем PendingIntent для уведомления
        // Проверяем версию Android и возможность планирования точных будильников
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent) // Планируем уведомление
        }
    }

    fun updateReminderTaked(reminder: Reminder, context: Context) {
        // Обновляем статус напоминания в списке
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders[index] = reminder // Обновляем список
        }

        // Обновляем статус в базе данных
        dbHelper.writableDatabase?.update(DatabaseHelper.TABLE_NAME, ContentValues().apply {
            put(DatabaseHelper.COLUMN_TAKED, reminder.taked)
        }, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(reminder.id.toString()))
    }













}
