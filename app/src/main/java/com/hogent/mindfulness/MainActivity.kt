package com.hogent.mindfulness

// Notificaties
// Settings
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.evernote.android.job.JobManager
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.UserApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercise_details.ExerciseDetailFragment
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.group.GroupFragment
import com.hogent.mindfulness.login.LoginActivity
import com.hogent.mindfulness.settings.SettingsActivity
import com.hogent.mindfulness.post.PostFragment
import com.hogent.mindfulness.profile.ProfileFragment
import com.hogent.mindfulness.services.NotifyJobCreator
import com.hogent.mindfulness.services.PeriodicNotificationJob
import com.hogent.mindfulness.sessions.FullscreenDialogWithAnimation
import com.hogent.mindfulness.sessions.SessionFragment
import com.hogent.mindfulness.sessions.SessionFragment.SessionAdapter.SessionAdapterOnUnlockSession
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SessionFragment.SessionAdapter.SessionAdapterOnClickHandler, SessionAdapterOnUnlockSession, ExercisesListFragment.ExerciseAdapter.ExerciseAdapterOnClickHandler {

    //initializing attributes
    private val mMindfullDB by lazy {
        MindfulnessDBHelper(this@MainActivity )
    }

    private val postService by lazy {
        ServiceGenerator.createService(PostApiService::class.java, this@MainActivity)
    }

    private lateinit var disposable: Disposable
    private lateinit var sessionFragment: SessionFragment
    private lateinit var groupFragment: GroupFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var postFragment: PostFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var exerciseDetailFragment: ExerciseDetailFragment
    private lateinit var fullscreenMonsterDialog : FullscreenDialogWithAnimation
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

        Log.i("LOGIN", "BEFORE REDIRECT")

        if (checkIfHasUser()){
            currentUser = mMindfullDB.getUser()!!
            if (checkIfHasGroup()) {
                groupFragment = GroupFragment()
                groupFragment.mMindfullDB = MindfulnessDBHelper(this)

                supportFragmentManager.beginTransaction()
                    .add(R.id.session_container, groupFragment)
                    .commit()
            } else {
                sessionFragment = SessionFragment()

                postFragment = PostFragment()
                profileFragment = ProfileFragment()

                supportFragmentManager.beginTransaction()
                    .add(R.id.session_container, sessionFragment)
                    .commit()
            }
        } else {
            Log.i("LOGIN", "REDIRECT")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        Log.i("LOGIN", "AFTER REDIRECT")

    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.i("ACTIVITY", "SAVE_INSTANCE_STATE")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("ACTIVITY", "RESTORE_INSTANCE_STATE")
    }
    private fun checkIfLoggedIn(): Boolean {
        val token = getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            .getString(getString(R.string.authTokenKey), null)
        return token == null
    }

    private fun checkIfHasGroup(): Boolean {
        return currentUser.group == null
    }

    private fun checkIfHasUser():Boolean {
        Log.i("DB_NULL_CHECK","${mMindfullDB.getUser()}")
        Log.i("DB_NULL_CHECK","${mMindfullDB}")
        return mMindfullDB.getUser() != null
    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("code")){
            if (checkIfHasGroup()) {
                val sharedPref =
                    getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                        .getString(getString(R.string.userIdKey), "")
                val user_group = Model.user_group(intent.getStringExtra("code"))
                val userService = ServiceGenerator.createService(UserApiService::class.java, this@MainActivity)
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
                val userService = ServiceGenerator.createService(UserApiService::class.java, this@MainActivity)

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

    override fun showMonsterDialog() {
        fullscreenMonsterDialog = FullscreenDialogWithAnimation()

        fullscreenMonsterDialog.show(supportFragmentManager.beginTransaction(), FullscreenDialogWithAnimation.TAG)

    }

    /**
     * Initialize ExerciseDetailFragment
     * Initialize manager in exerciseDetailFragment
     * Initialize exerciseId in exerciseDetailFragment
     * Add ExerciseDetailFragment to Activity
     */
    override fun onClickExercise(exercise: Model.Exercise) {
        exerciseDetailFragment = ExerciseDetailFragment()
        Log.i("EX ID", exercise._id)
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
        currentPost._id = newPost._id
        if (currentPost._id == "none" || currentPost._id == null){
            currentPost._id = null
            disposable = postService.addPost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { postResult ->  onPostResult(postResult) },
                    { error ->  showError(error.message) }
                )
        } else {
            disposable = postService.changePost(currentPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.i("oldPost", "$result") },
                    { error -> Log.i("ERRORCHECK",error.message) }
                )
        }
        return currentPost
    }

    fun onPostResult(savedPost:Model.Post){
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
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_feedback -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, postFragment)
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, profileFragment)
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
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
        when (itemThatWasClickedId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout -> {
                this@MainActivity.deleteDatabase("mindfulness.db")
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
