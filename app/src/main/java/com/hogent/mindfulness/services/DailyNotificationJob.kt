package com.hogent.mindfulness.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.evernote.android.job.DailyJob
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.settings.Notifications
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.floor

class DailyNotificationJob : DailyJob() {
    override fun onRunDailyJob(params: Params): DailyJobResult {

        val extras = params.extras

        val targetIntent = Intent(context, MainActivity::class.java)

        val title = extras.getString("title", "Mindfulness")
        val message = extras.getString("message", "")
        val channelId = extras.getString("channelId", "mindfulness")

        val notification = Notifications.getNotification(title, message, channelId, context, true, targetIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)

        return DailyJobResult.SUCCESS
    }

    companion object {
        val TAG = "MyDailyJob"

        // time is (hours * 60) + minutes
        fun scheduleJob(time: Int, duration: Long, title: String, message: String, channelId: String, updateCurrent: Boolean) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
            }

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val executionWindow = DailyExecutionWindow(hour, minute, (time/60).toLong(), (time%60).toLong(), duration)

            JobRequest.Builder(DailyNotificationJob.TAG)
                .setExecutionWindow(executionWindow.startMs, executionWindow.endMs)
                .setUpdateCurrent(updateCurrent)
                .setExtras(extras)
                .build()
                .schedule()
        }
    }
}