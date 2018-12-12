package com.hogent.mindfulness.services

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class NotifyJobCreator: JobCreator {

    override fun create(tag: String): Job? {
        return when (tag) {
            PeriodicNotificationJob.TAG -> PeriodicNotificationJob()
            DailyNotificationJob.TAG -> DailyNotificationJob()
            else -> null
        }
    }
}