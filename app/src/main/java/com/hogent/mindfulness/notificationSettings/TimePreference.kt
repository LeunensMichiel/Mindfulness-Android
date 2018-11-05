package com.hogent.mindfulness.notificationSettings

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.content.res.TypedArray
import android.util.AttributeSet
import com.hogent.mindfulness.R

/*
 * I used this https://github.com/code-troopers/android-betterpickers/issues/236
 */
class TimePreference(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    DialogPreference(context, attrs, defStyleAttr) {

    var mTime: Int = 0

    init {

    }



//    private val calendar = GregorianCalendar()
//    private lateinit var picker: TimePicker

//    override fun onCreateDialogView(): View {
//        picker = TimePicker(context)
//        return picker
//    }
//
//    override fun onBindDialogView(v: View) {
//        super.onBindDialogView(v)
//        picker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
//        picker.currentMinute = calendar.get(Calendar.MINUTE)
//    }

//    override fun onDialogClosed(positiveResult: Boolean) {
//        super.onDialogClosed(positiveResult)
//
//        if (positiveResult) {
//            calendar.set(Calendar.HOUR_OF_DAY, picker.currentHour)
//            calendar.set(Calendar.MINUTE, picker.currentMinute)
//
//            setSummary(summary)
//            if (callChangeListener(calendar.timeInMillis)) {
//                persistLong(calendar.timeInMillis)
//                notifyChanged()
//            }
//        }
//    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        // Default value from attribute. Fallback value is set to 0.
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(
        restorePersistedValue: Boolean,
        defaultValue: Any
    ) {
        // Read the value. Use the default value if it is not possible.
        mTime = (
            if (restorePersistedValue)
                getPersistedInt(mTime)
            else
                defaultValue as Int
        )
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.pref_dialog_time
    }


//    override fun getSummary(): CharSequence {
//        return DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
//    }

}