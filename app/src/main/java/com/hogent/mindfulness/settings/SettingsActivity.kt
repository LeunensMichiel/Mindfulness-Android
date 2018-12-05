package com.hogent.mindfulness.settings

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hogent.mindfulness.EULAFragment
import com.hogent.mindfulness.R
import com.hogent.mindfulness.group.GroupFragment


class SettingsActivity : AppCompatActivity(), SettingsFragment.OnPreferenceClickforFragment {

    private lateinit var groupFragment: GroupFragment
    private lateinit var emailFragmentFragment: ChangeEmailSettingsFragment
    private lateinit var passwordFragment: ChangePasswordFragment
    private lateinit var EULAFragment: EULAFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val model = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        val preferenceFragment = SettingsFragment()
        supportFragmentManager.beginTransaction().add(R.id.pref_container, preferenceFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreferenceClick(fragmentType: FragmentType) {
        when (fragmentType) {
            FragmentType.GROUP -> {
                groupFragment = GroupFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.pref_container, groupFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
            FragmentType.EMAIL -> {
                emailFragmentFragment = ChangeEmailSettingsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.pref_container, emailFragmentFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
            FragmentType.PASSWORD -> {
                passwordFragment = ChangePasswordFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.pref_container, passwordFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
            FragmentType.EULA -> {
                EULAFragment = EULAFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.pref_container, EULAFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
        }
    }

}
