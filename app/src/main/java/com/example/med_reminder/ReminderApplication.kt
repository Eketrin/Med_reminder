package com.example.med_reminder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import com.jakewharton.threetenabp.AndroidThreeTen

class ReminderApplication: Application() {
    companion object {
        const val channelName = "Reminders"
        const val channelDescription = "Channel for reminders"
        const val channelId = "reminders"
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    description = channelDescription
                    setSound(Settings.System.DEFAULT_NOTIFICATION_URI, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
                }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}