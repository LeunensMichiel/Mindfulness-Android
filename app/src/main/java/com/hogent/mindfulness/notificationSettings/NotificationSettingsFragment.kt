package com.hogent.mindfulness.notificationSettings

import android.os.Bundle
import com.hogent.mindfulness.R
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat


// I used this article https://medium.com/@eydryan/scheduling-notifications-on-android-with-workmanager-6d84b7f64613
// https://medium.com/mindorks/android-scheduling-background-services-a-developers-nightmare-c573807c2705
class NotificationSettingsFragment: PreferenceFragmentCompat()
{


    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)


    }

    private fun setPreferenceSummary(preference: Preference, value: Object) {
        val stringValue = value.toString()
        val key = preference.key


    }


}