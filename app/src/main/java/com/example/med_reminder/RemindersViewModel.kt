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

class RemindersViewModel: ViewModel() {
    lateinit var dbHelper: DatabaseHelper
    lateinit var alarmManager: AlarmManager

    var text by mutableStateOf("")
    var date by mutableStateOf("")
    var time by mutableStateOf("")

    var reminders = mutableStateListOf<Reminder>()
        private set

    fun addReminder(context: Context) {
        if(date.isEmpty() && time.isEmpty()) {
            return Toast.makeText(context, R.string.toast_datetime_error, Toast.LENGTH_LONG).show()
        } else if(text.isEmpty()) {
            return Toast.makeText(context, R.string.toast_text_error, Toast.LENGTH_LONG).show()
        }

        if(date.isEmpty()) date = Utils.getCurrentDate()
        if(time.isEmpty()) time = "12:00"

        val reminder = Reminder(Utils.getID(), text, date, time)
        reminders.add(reminder)

        dbHelper.writableDatabase?.insert(DatabaseHelper.TABLE_NAME, null, ContentValues().apply {
            put(DatabaseHelper.COLUMN_ID, reminder.id)
            put(DatabaseHelper.COLUMN_TEXT, reminder.text)
            put(DatabaseHelper.COLUMN_DATE, reminder.date)
            put(DatabaseHelper.COLUMN_TIME, reminder.time)
        })

        text = ""
        date = ""
        time = ""

        scheduleNotification(context, reminder.date, reminder.time, reminder.text, reminder.id)
        sortReminders()
        Toast.makeText(context, R.string.toast_reminder_created, Toast.LENGTH_LONG).show()
    }

    fun removeReminder(reminder: Reminder, context: Context) {
        reminders.remove(reminder)
        dbHelper.writableDatabase?.delete(DatabaseHelper.TABLE_NAME, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(reminder.id.toString()))
        alarmManager.cancel(Utils.getPendingIntent(context, reminder.id, reminder.text))
        Toast.makeText(context, R.string.toast_reminder_removed, Toast.LENGTH_LONG).show()
    }

    fun getReminders(context: Context) {
        reminders.clear()
        val cursor = dbHelper.readableDatabase?.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
        if(cursor?.moveToFirst() == true) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT))
                val date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME))

                val reminder = Reminder(id, text, date, time)
                if(Utils.isReminderInPast(date, time)) {
                    removeReminder(reminder, context)
                } else {
                    reminders.add(reminder)
                }
            } while(cursor.moveToNext())
        }
        cursor?.close()
        sortReminders()
    }

    fun sortReminders() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        reminders.sortWith(compareBy { reminder ->
            LocalDateTime.parse("${reminder.date} ${reminder.time}", formatter)
        })
    }

    fun scheduleNotification(context: Context, date: String, time: String, text: String, id: Int) {
        val dateTime = "$date $time"
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val triggerRime = sdf.parse(dateTime)?.time ?: return
        val pendingIntent = Utils.getPendingIntent(context, id, text)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerRime, pendingIntent)
        }
    }
}