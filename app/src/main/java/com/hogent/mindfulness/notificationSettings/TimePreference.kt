package com.hogent.mindfulness.notificationSettings

import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.content.res.TypedArray
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import java.util.*

/*
 * I used this https://github.com/code-troopers/android-betterpickers/issues/236
 */
class TimePreference(context: Context?, attrs: AttributeSet?) :
    DialogPreference(context, attrs) {

    private val calendar = GregorianCalendar()
    private lateinit var picker: TimePicker

    override fun onCreateDialogView(): View {
        picker = TimePicker(context)
        return picker
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        picker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        picker.currentMinute = calendar.get(Calendar.MINUTE)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, picker.currentHour)
            calendar.set(Calendar.MINUTE, picker.currentMinute)

            setSummary(summary)
            if (callChangeListener(calendar.timeInMillis)) {
                persistLong(calendar.timeInMillis)
                notifyChanged()
            }
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {

        if (restoreValue) {
            if (defaultValue == null) {
                calendar.timeInMillis = getPersistedLong(System.currentTimeMillis())
            } else {
                calendar.timeInMillis = getPersistedString(defaultValue as String) as Long
            }
        } else {
            if (defaultValue == null) {
                calendar.timeInMillis = (System.currentTimeMillis())
            } else {
                calendar.timeInMillis = defaultValue as Long
            }
        }
        setSummary(summary)
    }

    override fun getSummary(): CharSequence {
        return DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
    }

}