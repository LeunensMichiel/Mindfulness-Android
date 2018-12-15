package com.hogent.mindfulness.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.evernote.android.job.DailyJob
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.settings.Notifications
import java.util.concurrent.TimeUnit


class DailyNotificationJob : DailyJob() {
    override fun onRunDailyJob(params: Params): DailyJobResult {
            val extras = params.extras

            val targetIntent = Intent(context, MainActivity::class.java)

            val title = extras.getString("title", "Mindfulness")
            val message = extras.getString("message", "")
            val channelId = extras.getString("channelId", "mindfulness")
            val vibrate = extras.getBoolean("vibrate", false)

            val notification = Notifications.getNotification(title, message, channelId, context, vibrate, targetIntent)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0, notification)


        return DailyJobResult.SUCCESS
    }

    companion object {
        val TAG = "MyDailyJob"

        // time is (hours * 60) + minutes
        fun scheduleJob(time: Int, title: String, message: String, channelId: String, updateCurrent: Boolean, vibrate: Boolean) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
                putBoolean("vibrate", vibrate)
            }

//            val calendar = Calendar.getInstance()
//            val hour = calendar.get(Calendar.HOUR_OF_DAY)
//            val minute = calendar.get(Calendar.MINUTE)
//
//            val executionWindow = DailyExecutionWindow(hour, minute, (time/60).toLong(), (time%60).toLong(), duration)

            var t = TimeUnit.HOURS.toMillis((time/60).toLong())+TimeUnit.MINUTES.toMillis((time%60).toLong())
            DailyJob.schedule(JobRequest.Builder(DailyNotificationJob.TAG).setUpdateCurrent(updateCurrent).setExtras(extras), t, t+TimeUnit.HOURS.toMillis(1))

//            JobRequest.Builder(DailyNotificationJob.TAG)
//                .setExecutionWindow(executionWindow.startMs, executionWindow.endMs)
//                .setUpdateCurrent(updateCurrent)
//                .setExtras(extras)
//                .build()
//                .schedule()
        }
    }
}