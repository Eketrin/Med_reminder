package com.example.med_reminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen

// тут создаётся канал для наших уведомлений

class ReminderApplication: Application() {
    companion object {
        const val channelName = "Reminders" // Имя канала уведомлений, которое будет отображаться пользователю
        const val channelDescription = "Channel for reminders" // Описание канала уведомлений для информирования пользователя о его назначении
        const val channelId = "reminders" // Идентификатор канала уведомлений, необходимый для его создания и управления
    }

    override fun onCreate() { // Переопределение метода onCreate, который вызывается при создании приложения.
        super.onCreate() // Вызов метода родительского класса для корректной инициализации приложения.
        AndroidThreeTen.init(this) // Инициализация библиотеки AndroidThreeTen для работы с датами и временем, что необходимо для управления временными данными.

        // каналы уведомлений поддерживаются только с этой версии
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Создаем новый канал уведомлений
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    description = channelDescription // Устанавливаем описание канала, чтобы пользователь понимал его назначение
                    setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
                }
            // для управления уведомлениями
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Создаем канал уведомлений
            notificationManager.createNotificationChannel(channel)
        }
    }
}