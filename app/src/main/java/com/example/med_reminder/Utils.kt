package com.example.med_reminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class Utils {
    companion object {
        fun getID(): Int {
            return (Math.random() * 1000000).roundToInt()
        }
        // Добавляет ведущий ноль к числу, если оно меньше 10. Например, 5 станет "05".
        fun addZero(count: Int): String {
            return if (count < 10) "0$count" else count.toString()
        }
        // Возвращает текущую дату в формате "дд.мм.гггг".
        fun getCurrentDate(): String {
            val currentDate = Date()
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return formatter.format(currentDate)
        }
        // Проверяет, находится ли напоминание в прошлом, сравнивая его дату и время с текущими.
        fun isReminderInPast(date: String, time: String): Boolean {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val reminderDateTime = LocalDateTime.parse("$date $time", formatter)
            val now = LocalDateTime.now(ZoneId.systemDefault())
            return reminderDateTime.isBefore(now)
        }
        // Создает PendingIntent для напоминания, который будет отправлен в будущем.
        fun getPendingIntent(context: Context, id: Int, text: String): PendingIntent {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("text", text) // Передает текст напоминания
                putExtra("id", id)     // Передает уникальный идентификатор напоминания
            }
            return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}
