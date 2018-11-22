package com.hogent.mindfulness

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercise_details.ExerciseDetailFragment
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.login.LoginActivity
import com.hogent.mindfulness.exercise_details.*
import com.hogent.mindfulness.group.GroupFragment
import com.hogent.mindfulness.show_sessions.SessionFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
// Notificaties
import java.util.concurrent.TimeUnit
import com.hogent.mindfulness.services.NotifyJobCreator
import com.hogent.mindfulness.services.PeriodicNotificationJob
import com.evernote.android.job.JobManager
// Settings
import com.hogent.mindfulness.notification_settings.SettingsActivity
import org.jetbrains.anko.support.v4.toast


class MainActivity : AppCompatActivity(), SessionFragment.SessionAdapter.SessionAdapterOnClickHandler,
    ExercisesListFragment.ExerciseAdapter.ExerciseAdapterOnClickHandler {
    //initializing attributes
    private val mMindfullDB by lazy {
        MindfulnessDBHelper(this@MainActivity)
    }
    private val postService by lazy {
        ServiceGenerator.createService(PostApiService::class.java)
    }

    private lateinit var disposable: Disposable
    private lateinit var sessionFragment: SessionFragment
    private lateinit var groupFragment: GroupFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var exerciseDetailFragment: ExerciseDetailFragment
    private lateinit var currentUser: Model.User
    private var currentPost = Model.Post()
    /**
     * Set view to MainActivity
     * Set ItemSelectedListener for the navigation
     * initialize SessionFragment
     * add SessionFragment to activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Starts van notification service
        JobManager.create(this).addJobCreator(NotifyJobCreator())
        PeriodicNotificationJob.scheduleJob(
            TimeUnit.MINUTES.toMillis(15),
            "Mindfulness",
            "clean ur teeth boi",
            "mindfulness"
        )

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (checkIfLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        currentUser = mMindfullDB.getUser()!!
        Log.d("user", currentUser.toString())

        if (checkIfHasGroup()) {
            groupFragment = GroupFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.session_container, groupFragment)
                .commit()
        } else {
            sessionFragment = SessionFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.session_container, sessionFragment)
                .commit()
        }
    }

    private fun checkIfLoggedIn(): Boolean {
        val token = getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            .getString(getString(R.string.authTokenKey), null)
        return token == null
    }

    private fun checkIfHasGroup(): Boolean {
        return currentUser.group == null
    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("code")) {
            Log.d("code", intent.getStringExtra("code"))
            if (checkIfHasGroup()) {
                val sharedPref =
                    getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                        .getString(getString(R.string.userIdKey), "")
                val user_group = Model.user_group(intent.getStringExtra("code"))
                val userService = ServiceGenerator.createService(UserApiService::class.java,
                    getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                        .getString(getString(R.string.authTokenKey), null))
                val group = Model.Group(intent.getStringExtra("code"), "", "", null)

                mMindfullDB.addGroup(group)

                disposable = userService.updateUserGroup(sharedPref, user_group)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                    )
            } else {
                val sharedPref = getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                    .getString(getString(R.string.userIdKey), "")
                Log.d("user", sharedPref)
                val unlock_session = Model.unlock_session(sharedPref, intent.getStringExtra("code"))
                val userService = ServiceGenerator.createService(UserApiService::class.java,
                    getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                        .getString(getString(R.string.authTokenKey), null))

                disposable = userService.updateUser(unlock_session)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                    )
            }

        }

    }

    private fun showResult(result: Model.Result) {
        sessionFragment = SessionFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.session_container, sessionFragment)
            .commit()
    }

    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Initialize ExercisesListFragment
     * Initialize session in exerciseFragment
     * Add exerciseFragment to Activity
     */
    override fun onClick(session: Model.Session) {
        exerciseFragment = ExercisesListFragment()
        exerciseFragment.session = session
        currentPost.session_name = session.title
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, exerciseFragment)
            .addToBackStack("tag")
            .commit()
    }

    /**
     * Initialize ExerciseDetailFragment
     * Initialize manager in exerciseDetailFragment
     * Initialize exerciseId in exerciseDetailFragment
     * Add ExerciseDetailFragment to Activity
     */
    override fun onClickExercise(exercise: Model.Exercise) {
        exerciseDetailFragment = ExerciseDetailFragment()
        exerciseDetailFragment.manager = supportFragmentManager
        exerciseDetailFragment.exerciseId = exercise._id
        currentPost.exercise_name = exercise.title
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, exerciseDetailFragment)
            .addToBackStack("tag")
            .commit()
    }

    fun updatePost(page:Model.Page, description:String, newPost:Model.Post):Model.Post{
        currentPost.page_id = page._id
        currentPost.page_name = page.title
        currentPost.inhoud = description
        currentPost.user_id = currentUser._id

        Log.i("POST", "$newPost")
        if (newPost._id == "none"){
            disposable = postService.addPost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { postResult ->  onPostResult(postResult) },
                    { error ->  showError("FUCK") }
                )
        } else {
            disposable = postService.changePost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.i("oldPost", "$result") },
                    { error -> showError("MOTHERFUCK") }
                )
        }
        Log.i("POST", "${currentPost.session_name} > ${currentPost.exercise_name} > ${currentPost.page_name} - ${currentPost.page_id}")
        return currentPost
    }

    fun onPostResult(savedPost: Model.Post) {
        currentPost = savedPost
    }

    /**
     * Initialize NavigationListener
     * Specify Fragment to add to Activity via itemId in Navigation
     */
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

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // COMPLETED (9) Within onCreateOptionsMenu, use getMenuInflater().inflate to inflate the menu
        menuInflater.inflate(R.menu.logout_menu, menu)
        // COMPLETED (10) Return true to display your menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemThatWasClickedId = item.getItemId()
        if (itemThatWasClickedId == R.id.logout) {
            getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                .edit()
                .remove(getString(R.string.userIdKey))
                .remove(getString(R.string.authTokenKey))
                .apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


/*
   public fun creerParagraafFragment(description:String){
       var fragmentje = ParagraafTekst()

       val arg = Bundle()
       arg.putString("tekst", description)
       fragmentje.arguments = arg

       supportFragmentManager!!.beginTransaction().add(R.id.paragraafContainer, fragmentje ).commit()
       Log.d("testtesttest",description)
    } */

}
