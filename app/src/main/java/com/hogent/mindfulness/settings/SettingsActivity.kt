package com.hogent.mindfulness.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.group.GroupFragment


class SettingsActivity : AppCompatActivity(), SettingsFragment.OnPreferenceClickforFragment {

    private val mMindfullDB by lazy {
        MindfulnessDBHelper(this@SettingsActivity)
    }
    private lateinit var groupFragment: GroupFragment
    private lateinit var emailFragmentFragment: ChangeEmailSettingsFragment
    private lateinit var passwordFragment: ChangePasswordFragment
    private lateinit var EULAFragment: EULAFragment
    private lateinit var user : Model.User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val model = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        user = mMindfullDB.getUser()!!
        model.loadUser(user)
        model.user.observe(this, Observer {
            it?.let {
                // do some thing with the number
                Toast.makeText(this, "haha", Toast.LENGTH_LONG)
            }
        })

        model.user.value = mMindfullDB.getUser()
        model.user.value?.group = mMindfullDB.getGroup()

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
