package com.hogent.mindfulness.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.evernote.android.job.DailyJob
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.settings.Notifications

class SingleJob : Job() {
    override fun onRunJob(params: Params): Result {

        val extras = params.extras

        val targetIntent = Intent(context, MainActivity::class.java)

        val title = extras.getString("title", "Mindfulness")
        val message = extras.getString("message", "")
        val channelId = extras.getString("channelId", "mindfulness")

        val notification = Notifications.getNotification(title, message, channelId, context, false, targetIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)

        return Result.SUCCESS
    }

    companion object {

        val TAG: String = "job_one"

        fun scheduleJob(duration: Long, title: String, message: String, channelId: String) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
            }

            JobRequest.Builder(SingleJob.TAG)
                .setExtras(extras)
                .build()
                .schedule()
        }
    }
}