package com.hogent.mindfulness.settings

import android.os.Build
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.text.format.DateFormat
import android.view.View
import kotlinx.android.synthetic.main.pref_dialog_time.*
import android.os.Bundle
import kotlinx.android.synthetic.main.pref_dialog_time.view.*


class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

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

            view.edit.setIs24HourView(is24hour)
            view.edit.currentHour = hours
            view.edit.currentMinute = minutes
        }
    }


    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {

            var hours: Int
            var minutes: Int
            // generate value to save
            if (Build.VERSION.SDK_INT >= 23) {
                hours = edit.hour
                minutes = edit.minute
            } else {
                hours = edit.currentHour
                minutes = edit.currentMinute
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