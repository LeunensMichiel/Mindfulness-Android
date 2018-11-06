package com.hogent.mindfulness

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.evernote.android.job.JobManager
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExerciseAdapter
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.notification_settings.SettingsActivity
import com.hogent.mindfulness.oefeningdetails.*
import com.hogent.mindfulness.services.NotifJobCreator
import com.hogent.mindfulness.services.PeriodicNotificationJob
import com.hogent.mindfulness.show_sessions.SessionAdapter
import com.hogent.mindfulness.show_sessions.SessionFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SessionAdapter.SessionAdapterOnClickHandler, ExerciseAdapter.ExerciseAdapterOnClickHandler {

    private lateinit var sessionFragment: SessionFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var oefeningDetailFragment: OefeningDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Starts van notification service
        JobManager.create(this).addJobCreator(NotifJobCreator())

        PeriodicNotificationJob.scheduleJob(TimeUnit.MINUTES.toMillis(15),"Mindfulness", "this is a notification about mindfulness", "mindfulness")

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        sessionFragment = SessionFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.session_container, sessionFragment)
            .commit()
    }

    override fun onClick(session: Model.Session) {
        exerciseFragment = ExercisesListFragment()
        exerciseFragment.session = session

        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, exerciseFragment)
            .commit()
    }

    override fun onClickExercise(exercise: Model.Exercise) {
        oefeningDetailFragment = OefeningDetailFragment()

        oefeningDetailFragment.manager = supportFragmentManager
        oefeningDetailFragment.exerciseId = exercise._id

        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, oefeningDetailFragment)
            .commit()
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, sessionFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        false
    }

}
