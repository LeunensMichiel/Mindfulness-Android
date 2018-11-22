package com.hogent.mindfulness.notification_settings

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.hogent.mindfulness.R

class Notifications {

    companion object {

        fun getNotification(
            title: String,
            message: String,
            channelId: String,
            context: Context,
            targetIntent: Intent
        ): Notification {

            val contentIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val mBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.FLAG_FOREGROUND_SERVICE)
                .setContentIntent(contentIntent)

            return mBuilder.build()
        }
    }
}