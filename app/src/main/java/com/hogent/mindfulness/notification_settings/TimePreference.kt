package com.hogent.mindfulness.notification_settings

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.content.res.TypedArray
import android.util.AttributeSet
import com.hogent.mindfulness.R

/*
 * I used this https://github.com/code-troopers/android-betterpickers/issues/236
 */
class TimePreference(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    var mTime: Int = 0
        set(value) {
            field = value

            persistInt(value)
        }

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

}