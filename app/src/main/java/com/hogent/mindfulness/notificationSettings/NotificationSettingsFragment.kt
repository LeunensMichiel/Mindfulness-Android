package com.hogent.mindfulness.notificationSettings

import android.os.Bundle
import android.support.v4.app.DialogFragment
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

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        lateinit var dialogFragment: DialogFragment
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.key)
        }

        if (dialogFragment!= null) {

        }else {
            super.onDisplayPreferenceDialog(preference)

        }

    }




}