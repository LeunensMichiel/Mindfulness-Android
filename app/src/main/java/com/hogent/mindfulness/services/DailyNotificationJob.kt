package com.hogent.mindfulness.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.evernote.android.job.DailyJob
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.settings.Notifications

class DailyNotificationJob : DailyJob() {

    override fun onRunDailyJob(params: Params): DailyJobResult {

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

        return DailyJobResult.SUCCESS
    }

    companion object {

        val TAG = "MyDailyJob"

        fun scheduleJob(startScheduleMs: Long, endScheduleMs: Long, title: String, message: String, channelId: String) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
            }

            val job = JobRequest.Builder(PeriodicNotificationJob.TAG)
                .setUpdateCurrent(true)
                .setExtras(extras)

            DailyJob.schedule(job, startScheduleMs, endScheduleMs)

        }


    }

}