package com.hogent.mindfulness

// Notificaties
// Settings
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.evernote.android.job.JobManager
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessDBHelper
import com.hogent.mindfulness.data.PostApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.data.API.UserApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.*
import com.hogent.mindfulness.exercise_details.ExerciseDetailFragment
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.group.GroupFragment
import com.hogent.mindfulness.login.LoginActivity
import com.hogent.mindfulness.login.LoginFragment
import com.hogent.mindfulness.login.RegisterFragment
import com.hogent.mindfulness.post.PostFragment
import com.hogent.mindfulness.profile.ProfileFragment
import com.hogent.mindfulness.services.NotifyJobCreator
import com.hogent.mindfulness.services.PeriodicNotificationJob
import com.hogent.mindfulness.sessions.FullscreenDialogWithAnimation
import com.hogent.mindfulness.sessions.SessionFragment
import com.hogent.mindfulness.sessions.SessionFragment.SessionAdapter.SessionAdapterOnUnlockSession
import com.hogent.mindfulness.settings.SettingsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.feedback_popup.*
import kotlinx.android.synthetic.main.feedback_popup.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SessionAdapterOnUnlockSession {

    //initializing attributes
    private val mMindfullDB by lazy {
        MindfulnessDBHelper(this@MainActivity )
    }

    private val postService by lazy {
        ServiceGenerator.createService(PostApiService::class.java, this@MainActivity)
    }

    private lateinit var disposable: Disposable
    lateinit var loginFragment : LoginFragment
    private lateinit var sessionFragment: SessionFragment
    private lateinit var groupFragment: GroupFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var postFragment: PostFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var exerciseDetailFragment: ExerciseDetailFragment
    private lateinit var feedbackDialog:Dialog
    private lateinit var fullscreenMonsterDialog : FullscreenDialogWithAnimation
    private var currentUser: Model.User? = null
    private lateinit var userView: UserViewModel
    private lateinit var sessionView: SessionViewModel
    private lateinit var exView:ExerciseViewModel
    private lateinit var pageView:PageViewModel
    private lateinit var stateView:StateViewModel
    private var currentPost = Model.Post()
    /**
     * Set view to MainActivity
     * Set ItemSelectedListener for the navigation
     * initialize SessionFragment
     * add SessionFragment to activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.visibility = View.GONE
        userView = ViewModelProviders.of(this).get(UserViewModel::class.java)
        sessionView = ViewModelProviders.of(this).get(SessionViewModel::class.java)
        exView = ViewModelProviders.of(this).get(ExerciseViewModel::class.java)
        pageView = ViewModelProviders.of(this).get(PageViewModel::class.java)
        stateView = ViewModelProviders.of(this).get(StateViewModel::class.java)

        stateView.viewState.observe(this, Observer {
            when(it!!){
                "EXERCISE_VIEW" -> {
                    if (!::exerciseFragment.isInitialized){
                        exerciseFragment = ExercisesListFragment()
                    }
                    exerciseFragment.session = sessionView.selectedSession.value!!
                    currentPost.session_name = sessionView.selectedSession.value!!.title
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, exerciseFragment)
                        .addToBackStack("tag")
                        .commit()
                }
                "PAGE_VIEW" -> {
                    if (!::exerciseDetailFragment.isInitialized) {
                        exerciseDetailFragment = ExerciseDetailFragment()
                        exerciseDetailFragment.manager = supportFragmentManager
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, exerciseDetailFragment)
                        .addToBackStack("tag")
                        .commit()
                }
            }
        })

        stateView.dialogState.observe(this, Observer {
            when(it!!) {
                "FEEDBACK_DIALOG" -> {
                    if (!::feedbackDialog.isInitialized){
                        feedbackDialog = Dialog(this)
                        feedbackDialog.setContentView(R.layout.feedback_popup)
                        feedbackDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        feedbackDialog.feedback_cancelBtn.onClick { feedbackDialog.hide() }
                        feedbackDialog.feedback_sendBtn.onClick {
                            sessionView.saveFeedBack(Model.Feedback(Date(), feedbackDialog.feedback_description.text.toString()))
                            feedbackDialog.hide()
                        }
                        feedbackDialog.feedback_uitschrijvenBtn.onClick {
                            userView.updateFeedback()
                            feedbackDialog.hide()
                        }
                    }
                    feedbackDialog.feedback_namesessie.text = sessionView.selectedSession?.value?.title
                    feedbackDialog.show()
                }
            }
        })

        userView.dbUser.observe(this, Observer<Model.User?> {
            if (it == null){
                navigation.visibility = View.GONE
                loginFragment = LoginFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, loginFragment)
                    .commit()
            } else {
                if (it.group != null) {
                    navigation.visibility = View.VISIBLE
                    sessionFragment = SessionFragment()

                    postFragment = PostFragment()
                    profileFragment = ProfileFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, sessionFragment)
                        .commit()
                } else  {
                    navigation.visibility = View.GONE
                    groupFragment = GroupFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, groupFragment)
                        .commit()
                }
            }

        })

        userView.toastMessage.observe(this, Observer {
            if (it != null)
                toast(it).show()
        })

        sessionView.selectedSession.observe(this, Observer<Model.Session> {
            if (it != null){
                if (it.unlocked){
                    exView.session_id = it._id
                    exView.retrieveExercises()
                } else {
                    toast("Sessie nog niet geopend.").show()
                }
            }
        })

        sessionView.sessionToast.observe(this, Observer {
            if (it != null){
                toast(it).show()
            }
        })

        exView.selectedExercise.observe(this, Observer {
            if (it != null){
                pageView.exercise_id = it._id
                stateView.viewState?.value = "PAGE_VIEW"
            }
        })

        pageView.pageError.observe(this, Observer {
            if (it != null && !it.equals(Model.errorMessage())){
                toast(it.error).show()
            }
        })

        // Starts van notification service
        JobManager.create(this).addJobCreator(NotifyJobCreator())
        PeriodicNotificationJob.scheduleJob(
            TimeUnit.MINUTES.toMillis(15),
            "Mindfulness",
            "clean ur teeth boi",
            "mindfulness"
        )

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    //This function replqces the register fragment back with the login fragment
    fun toLogin(v:View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, loginFragment)
            .commit()
    }

    /**
     * This function replaces the login fragment with the register fragment
     */
    fun toRegister(v:View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, RegisterFragment())
            .commit()
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
        return currentUser!!.group == null
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
//    override fun onClickExercise(exercise: Model.Exercise) {
//        exerciseDetailFragment = ExerciseDetailFragment()
//        Log.i("EX ID", exercise._id)
//        exerciseDetailFragment.manager = supportFragmentManager
//        exerciseDetailFragment.exerciseId = exercise._id
//        currentPost.exercise_name = exercise.title
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.session_container, exerciseDetailFragment)
//            .addToBackStack("tag")
//            .commit()
//    }

    fun updatePost(page:Model.Page, description:String, newPost:Model.Post):Model.Post{
        currentPost.page_id = page._id
        currentPost.page_name = page.title
        currentPost.inhoud = description
        currentPost.user_id = currentUser!!._id
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
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.session_container, sessionFragment)
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_feedback -> {
                supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.session_container, postFragment)
                    .addToBackStack(null)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
}
