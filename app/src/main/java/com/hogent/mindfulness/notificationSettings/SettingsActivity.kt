package com.hogent.mindfulness.notificationSettings

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hogent.mindfulness.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.setContentView(R.layout.activity_settings)

        val preferenceFragment = NotificationSettingsFragment()
        supportFragmentManager.beginTransaction().add(R.id.pref_container, preferenceFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.home){
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}
