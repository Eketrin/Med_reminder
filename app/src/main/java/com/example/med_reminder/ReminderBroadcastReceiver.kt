package com.example.med_reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

//тут мы готовим уведомление к отправке и отправляем его

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

            // Создаем Intent для удаления уведомления
            val dismissIntent = Intent(context, DismissNotificationReceiver::class.java).apply {
                putExtra("id", id) // Передаем идентификатор уведомления
            }
            val dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                id ?: 0, // Уникальный идентификатор для PendingIntent
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Флаги для PendingIntent
            )

            // Создаем уведомление с использованием NotificationCompat.Builder
            val builder = NotificationCompat.Builder(context, ReminderApplication.channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.resources.getString(R.string.new_reminder)) // Заголовок
                .setContentText(text) // Устанавливаем текст уведомления
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Приоритет
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Видимость
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Звук
                .addAction(
                    R.drawable.ic_read,
                    "Прочитано",
                    dismissPendingIntent
                ) // Кнопка "Прочитано"

            // Проверка на разрешение на отправку уведомлений
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return // Если нет, выходим из метода
            }

            // Отправляем уведомление
            notificationManager.notify(id!!, builder.build())
        }
    }
}

// Новый BroadcastReceiver для обработки нажатия на кнопку "Прочитано"
class DismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val id = intent.getIntExtra("id", 0)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(id) // Удаляем уведомление с указанным id
        }
    }
}


