package com.hogent.mindfulness.settings

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import com.evernote.android.job.JobManager
import com.hogent.mindfulness.BuildConfig
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.hogent.mindfulness.services.DailyNotificationJob
import java.util.concurrent.TimeUnit


// I used this article https://medium.com/@eydryan/scheduling-notifications-on-android-with-workmanager-6d84b7f64613
// https://medium.com/mindorks/android-scheduling-background-services-a-developers-nightmare-c573807c2705
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var userView: UserViewModel
    private lateinit var dbUser: Model.User

    interface OnPreferenceClickforFragment {
        fun onPreferenceClick(fragmentType: FragmentType)
    }

    var clicker: OnPreferenceClickforFragment? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        clicker = context as? OnPreferenceClickforFragment
        if (clicker == null) {
            throw ClassCastException("$context must implement OnPreferenceClickForFragment")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.home -> fragmentManager?.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)

        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid activity.")
        dbUser = userView.dbUser.value!!

        val sharedPref =
            context!!.getSharedPreferences("com.hogent.mindfulness_preferences", Context.MODE_PRIVATE)

        val versionPreference = findPreference(getString(R.string.pref_key_version))
        versionPreference.summary = BuildConfig.VERSION_NAME

        //Voor de huidige switches enzo in te stellen op de huidige waaren en de changelisteners als er iets moet gebeuren on change van die items
        val wantsNotifications =
            findPreference("switch_notifications") as android.support.v14.preference.SwitchPreference
        wantsNotifications.isChecked = sharedPref.getBoolean("switch_notifications", true)
        wantsNotifications.setOnPreferenceChangeListener { preference, value ->
            wantsNotifications.isChecked = value as Boolean
            sharedPref.edit().putBoolean("switch_notifications", value)
            if (value) {
                if (dbUser.unlocked_sessions.size > 0) {
                    DailyNotificationJob.scheduleJob(
                        sharedPref.getInt("pref_time", 12 * 60),
                        "Mindfulness",
                        "Denk aan " + dbUser.unlocked_sessions.get(dbUser.unlocked_sessions.size - 1),
                        "mindfulness",
                        true,
                        sharedPref.getBoolean("key_vibrate", false)
                    )
                }
            } else {
                JobManager.instance().cancelAllForTag("MyDailyJob")
            }
            true
        }
        val wantsVibrate =
            findPreference("key_vibrate") as android.support.v14.preference.SwitchPreference
        wantsVibrate.isChecked = sharedPref.getBoolean("key_vibrate", true)
        wantsVibrate.setOnPreferenceChangeListener { preference, value ->
            wantsVibrate.isChecked = value as Boolean
            sharedPref.edit().putBoolean("key_vibrate", value)
            if (dbUser.unlocked_sessions.size > 0) {
                DailyNotificationJob.scheduleJob(
                    sharedPref.getInt("pref_time", 12 * 60),
                    "Mindfulness",
                    "Denk aan " + dbUser.unlocked_sessions.get(dbUser.unlocked_sessions.size - 1),
                    "mindfulness",
                    true,
                    value
                )
            }

            true
        }
        val wantsFeedbackPreference =
            findPreference(getString(R.string.pref_feedback)) as android.support.v14.preference.SwitchPreference
        wantsFeedbackPreference.isChecked = dbUser.feedbackSubscribed
        wantsFeedbackPreference.setOnPreferenceChangeListener { preference, value ->
            wantsFeedbackPreference.isChecked = value as Boolean
            dbUser.feedbackSubscribed = value
            userView.updateFeedback(value)
            true
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference!!.key) {
            getString(R.string.pref_groep) -> {
                clicker!!.onPreferenceClick(FragmentType.GROUP)
                true
            }
            getString(R.string.pref_email) -> {
                clicker!!.onPreferenceClick(FragmentType.EMAIL)
                true
            }
            getString(R.string.pref_wachtwoord) -> {
                clicker!!.onPreferenceClick(FragmentType.PASSWORD)
                true
            }
            getString(R.string.pref_privacy) -> {
                clicker!!.onPreferenceClick(FragmentType.EULA)
                true
            }
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        lateinit var dialogFragment: DialogFragment

        when (preference) {
            is TimePreference -> dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.key)
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(
                this.getFragmentManager(),
                "android.support.v7.preference" +
                        ".PreferenceFragment.DIALOG"
            );
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle("Instellingen")

    }
}

