package com.example.med_reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null) {
            val text = intent?.getStringExtra("text") ?: "Reminder Text"
            val id = intent?.getIntExtra("id", 0)
            val notificationManager = NotificationManagerCompat.from(context)
            val builder = NotificationCompat.Builder(context, ReminderApplication.channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.resources.getString(R.string.new_reminder))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            if(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notificationManager.notify(id!!, builder.build())
        }
    }
}