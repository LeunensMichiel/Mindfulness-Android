package com.hogent.mindfulness.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.hogent.mindfulness.MainActivity
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.hogent.mindfulness.settings.Notifications


class PeriodicNotificationJob : Job() {
    // https://medium.com/mindorks/android-scheduling-background-services-a-developers-nightmare-c573807c2705

    override fun onRunJob(params: Params): Result {

        val extras = params.extras

        val targetIntent = Intent(context, MainActivity::class.java)

        val title = extras.getString("title", "Mindfulness")
        val message = extras.getString("message", "")
        val channelId = extras.getString("channelId", "mindfulness")

        val notification = Notifications.getNotification(title, message, channelId, context, targetIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Mindfulness notifications",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }

        notificationManager.notify(0, notification)

        return Result.SUCCESS
    }

    companion object {

        val TAG: String = "job_notif_sync"

        fun scheduleJob(duration: Long, title: String, message: String, channelId: String) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
            }

            JobRequest.Builder(PeriodicNotificationJob.TAG)
                .setPeriodic(duration)
                .setUpdateCurrent(true)
                .setExtras(extras)
                .build()
                .schedule()
        }
    }

}