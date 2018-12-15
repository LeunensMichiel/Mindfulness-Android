package com.hogent.mindfulness.settings

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Build
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.text.format.DateFormat
import android.view.View
import kotlinx.android.synthetic.main.pref_dialog_time.*
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.hogent.mindfulness.services.DailyNotificationJob
import kotlinx.android.synthetic.main.pref_dialog_time.view.*
import java.util.concurrent.TimeUnit


// https://medium.com/@JakobUlbrich/building-a-settings-screen-for-android-part-3-ae9793fd31ec

class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    lateinit var mTimePicker: TimePicker
    private lateinit var userView: UserViewModel
    private lateinit var dbUser: Model.User

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        userView = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        }?: throw Exception("Invalid activity.")
        dbUser = userView.dbUser.value!!
        mTimePicker = view.findViewById(R.id.edit) as TimePicker

        // Get the time from the related Preference
        var minutesAfterMidnight: Int? = null

        if (preference is TimePreference) {
            minutesAfterMidnight = (preference as TimePreference).mTime
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60
            val is24hour = DateFormat.is24HourFormat(context)

            mTimePicker.setIs24HourView(is24hour)
            mTimePicker.currentHour = hours
            mTimePicker.currentMinute = minutes
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {

            var hours: Int
            var minutes: Int
            // generate value to save
            if (Build.VERSION.SDK_INT >= 23) {
                hours = mTimePicker.hour
                minutes = mTimePicker.minute
            } else {
                hours = mTimePicker.currentHour
                minutes = mTimePicker.currentMinute
            }

            val minutesAfterMidnight = (hours * 60) + minutes

            // Get the related Preference and save the value
            if (preference is TimePreference) {
                val timePreference: TimePreference = preference as TimePreference
                // This allows the client to ignore the user value.
                if (preference.callChangeListener(minutesAfterMidnight)) {
                    // Save the value
                    timePreference.mTime = minutesAfterMidnight
                }
            }

            val sharedPref =
                context!!.getSharedPreferences("com.hogent.mindfulness_preferences", Context.MODE_PRIVATE)
            if(dbUser.unlocked_sessions.size > 0 ) {
                DailyNotificationJob.scheduleJob(
                    minutesAfterMidnight,
                    "Mindfulness",
                    "Denk aan "+dbUser.unlocked_sessions.get(dbUser.unlocked_sessions.size-1),
                    "mindfulness",
                    true,
                    sharedPref.getBoolean("key_vibrate", false)
                )
            }
        }
    }

    companion object {
        fun newInstance(
            key: String
        ): TimePreferenceDialogFragmentCompat {
            val fragment = TimePreferenceDialogFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b

            return fragment
        }
    }

}