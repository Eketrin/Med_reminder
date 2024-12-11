package com.example.med_reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Проверяем, что контекст не равен null, чтобы избежать ошибок
        if (context != null) {
            // Извлекаем текст напоминания из Intent. Если он отсутствует, используем значение по умолчанию.
            val text = intent?.getStringExtra("text") ?: "Reminder Text"
            // Извлекаем идентификатор напоминания из Intent. Если он отсутствует, используем значение по умолчанию (0).
            val id = intent?.getIntExtra("id", 0)

            // Получаем экземпляр NotificationManagerCompat для управления уведомлениями
            val notificationManager = NotificationManagerCompat.from(context)

            // Создаем уведомление с использованием NotificationCompat.Builder
            val builder = NotificationCompat.Builder(context, ReminderApplication.channelId)
                .setSmallIcon(R.drawable.ic_notification) // Устанавливаем иконку уведомления
                .setContentTitle(context.resources.getString(R.string.new_reminder)) // Устанавливаем заголовок уведомления
                .setContentText(text) // Устанавливаем текст уведомления
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Устанавливаем высокий приоритет для уведомления
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Устанавливаем видимость уведомления
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Устанавливаем звук уведомления

            // Проверяем, есть ли разрешение на отправку уведомлений
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return // Если разрешение не предоставлено, выходим из метода
            }

            // Отправляем уведомление с заданным идентификатором
            notificationManager.notify(id!!, builder.build())
        }
    }
}
