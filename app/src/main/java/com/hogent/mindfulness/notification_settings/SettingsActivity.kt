package com.hogent.mindfulness.notification_settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hogent.mindfulness.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
