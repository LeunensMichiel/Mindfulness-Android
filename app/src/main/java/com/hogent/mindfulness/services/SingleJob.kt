package com.hogent.mindfulness.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.settings.Notifications
import java.util.*

class SingleJob : Job() {
    override fun onRunJob(params: Params): Result {
        val extras = params.extras

        val title = extras.getString("title", "Mindfulness")
        val message = extras.getString("message", "")
        val channelId = extras.getString("channelId", "mindfulness")
        val sessionID = extras.getString("sessionID", "")

        val targetIntent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        targetIntent.putExtra("sessionID", sessionID)
//        targetIntent.setAction(Intent.ACTION_MAIN)
//        targetIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val notification = Notifications.getNotification(title, message, channelId, context, false, targetIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)

        return Result.SUCCESS
    }

    companion object {

        val TAG: String = "job_notif_sync"

        fun scheduleJob(inMinutes: Int, duration: Long, title: String, message: String, channelId: String, sessionID: String?) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
                putString("sessionID", sessionID)
            }

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val executionWindow = DailyExecutionWindow(hour, minute, hour+(inMinutes/60).toLong(), minute+(inMinutes%60).toLong(), duration)

            JobRequest.Builder(SingleJob.TAG)
                .setExecutionWindow(executionWindow.startMs, executionWindow.endMs)
                .setUpdateCurrent(true)
                .setExtras(extras)
                .build()
                .schedule()
        }

        fun scheduleJob(atTime: Int, duration: Long, title: String, message: String, channelId: String) {
            val extras = PersistableBundleCompat()
            extras.apply {
                putString("title", title)
                putString("message", message)
                putString("channelId", channelId)
            }

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val executionWindow = DailyExecutionWindow(hour, minute, (atTime/60).toLong(), (atTime%60).toLong(), duration)

            JobRequest.Builder(SingleJob.TAG)
                .setExecutionWindow(executionWindow.startMs, executionWindow.endMs)
                .setUpdateCurrent(true)
                .setExtras(extras)
                .build()
                .schedule()
        }
    }
}