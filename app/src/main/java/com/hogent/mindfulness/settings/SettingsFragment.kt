package com.hogent.mindfulness.settings

import android.content.Context
import android.os.Bundle
import android.preference.SwitchPreference
import android.support.v4.app.DialogFragment
import com.hogent.mindfulness.R
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.ListView
import com.hogent.mindfulness.BuildConfig


// I used this article https://medium.com/@eydryan/scheduling-notifications-on-android-with-workmanager-6d84b7f64613
// https://medium.com/mindorks/android-scheduling-background-services-a-developers-nightmare-c573807c2705
class SettingsFragment : PreferenceFragmentCompat() {

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


    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)

        val sharedPref =
            context!!.getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)

        val versionPreference = findPreference(getString(R.string.pref_key_version))
        versionPreference.summary = BuildConfig.VERSION_NAME

        //Voor de huidige switches enzo in te stellen op de huidige waaren en de changelisteners als er iets moet gebeuren on change van die items
        val wantsFeedbackPreference =
            findPreference(getString(R.string.pref_feedback)) as android.support.v14.preference.SwitchPreference
        val feedback = sharedPref.getBoolean(getString(R.string.wantsFeedback), true)
        wantsFeedbackPreference.isChecked = feedback

        wantsFeedbackPreference.setOnPreferenceChangeListener { preference, value ->
            wantsFeedbackPreference.isChecked = value as Boolean
            sharedPref.edit().putBoolean(getString(R.string.wantsFeedback), value).apply()
            //TODO - User moet hier veranderd worden
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
}

