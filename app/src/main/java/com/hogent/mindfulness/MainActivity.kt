package com.hogent.mindfulness

// Notificaties
// Settings
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.evernote.android.job.JobManager
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.*
import com.hogent.mindfulness.exercise_details.ExerciseDetailFragment
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.group.GroupFragment
import com.hogent.mindfulness.login.LoginFragment
import com.hogent.mindfulness.login.RegisterFragment
import com.hogent.mindfulness.post.PostFragment
import com.hogent.mindfulness.profile.ProfileFragment
import com.hogent.mindfulness.services.NotifyJobCreator
import com.hogent.mindfulness.services.PeriodicNotificationJob
import com.hogent.mindfulness.sessions.FullscreenDialogWithAnimation
import com.hogent.mindfulness.sessions.SessionFragment
import com.hogent.mindfulness.sessions.SessionFragment.SessionAdapter.SessionAdapterOnUnlockSession
import com.hogent.mindfulness.settings.*
import com.hogent.mindfulness.settings.SettingsFragment.OnPreferenceClickforFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.feedback_popup.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), SessionAdapterOnUnlockSession, OnPreferenceClickforFragment {
    //initializing attributes
    lateinit var loginFragment: LoginFragment
    private lateinit var sessionFragment: SessionFragment
    private lateinit var groupFragment: GroupFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var postFragment: PostFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var exerciseDetailFragment: ExerciseDetailFragment
    private lateinit var emailFragmentFragment: ChangeEmailSettingsFragment
    private lateinit var passwordFragment: ChangePasswordFragment
    private lateinit var EULAFragment: EULAFragment
    private lateinit var feedbackDialog: Dialog
    private lateinit var fullscreenMonsterDialog: FullscreenDialogWithAnimation
    private var currentUser: Model.User? = null
    private lateinit var userView: UserViewModel
    private lateinit var sessionView: SessionViewModel
    private lateinit var exView: ExerciseViewModel
    private lateinit var pageView: PageViewModel
    private lateinit var stateView: StateViewModel
    private lateinit var postView: PostViewModel
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

        supportActionBar!!.elevation = 0F

        navigation.visibility = View.GONE
        userView = ViewModelProviders.of(this).get(UserViewModel::class.java)
        sessionView = ViewModelProviders.of(this).get(SessionViewModel::class.java)
        exView = ViewModelProviders.of(this).get(ExerciseViewModel::class.java)
        pageView = ViewModelProviders.of(this).get(PageViewModel::class.java)
        stateView = ViewModelProviders.of(this).get(StateViewModel::class.java)
        postView = ViewModelProviders.of(this).get(PostViewModel::class.java)

        stateView.viewState.observe(this, Observer {
            when (it!!) {
                "EXERCISE_VIEW" -> {
                    if (!::exerciseFragment.isInitialized) {
                        exerciseFragment = ExercisesListFragment()
                    }
                    sessionView.loadImages()
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
            when (it!!) {
                "FEEDBACK_DIALOG" -> {
                    if (!::feedbackDialog.isInitialized) {
                        feedbackDialog = Dialog(this)
                        feedbackDialog.setContentView(R.layout.feedback_popup)
                        feedbackDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        feedbackDialog.feedback_cancelBtn.onClick { feedbackDialog.hide() }
                        feedbackDialog.feedback_sendBtn.onClick {
                            sessionView.saveFeedBack(
                                Model.Feedback(
                                    Date(),
                                    feedbackDialog.feedback_description.text.toString()
                                )
                            )
                            feedbackDialog.hide()
                        }
                        feedbackDialog.feedback_uitschrijvenBtn.onClick {
                            userView.updateFeedback(false)
                            feedbackDialog.hide()
                        }
                    }
                    feedbackDialog.feedback_namesessie.text = sessionView.selectedSession?.value?.title
                    feedbackDialog.show()
                }
            }
        })

        userView.dbUser.observe(this, Observer<Model.User?> {
            if (it == null) {
                navigation.visibility = View.GONE
                showAcionBar(false)
                loginFragment = LoginFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, loginFragment)
                    .commit()
            } else {
                showAcionBar(true)
                if (it.group != null) {
                    sessionView.resetunlockedSession()
                    navigation.visibility = View.VISIBLE
                    sessionFragment = SessionFragment()

                    postFragment = PostFragment()
                    profileFragment = ProfileFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, sessionFragment)
                        .commit()
                } else {
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
            if (it != null) {
                if (it.unlocked) {
                    exView.session_id = it._id
                    exView.retrieveExercises()
                    pageView.session_name = it.title
                } else {
                    toast("Sessie nog niet geopend.").show()
                }
            }
        })

        sessionView.sessionToast.observe(this, Observer {
            if (it != null) {
                toast(it).show()
            }
        })

        exView.selectedExercise.observe(this, Observer {
            if (it != null) {
                pageView.exercise_id = it._id
                stateView.viewState?.value = "PAGE_VIEW"
                pageView.ex_name = it.title
            }
        })

        pageView.pageError.observe(this, Observer {
            if (it != null && !it.equals(Model.errorMessage())) {
                toast(it.error).show()
            }
        })

        postView.error.observe(this, Observer {
            if (it != null && !it.equals(Model.errorMessage())) {
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
    fun toLogin(v: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, LoginFragment())
            .commit()
    }

    /**
     * This function replaces the login fragment with the register fragment
     */
    fun toRegister(v: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, RegisterFragment())
            .commit()
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
            if (userView.userRepo.user.value?.group == null) {
                userView.addGroup(Model.user_group(intent.getStringExtra("code")))
            } else {
                if (userView.userRepo.user.value?.unlocked_sessions!!.contains(intent.getStringExtra("code"))) {
                    Toast.makeText(this, "Sessie reeds vrijgespeeld", Toast.LENGTH_SHORT).show()
                }
                else {
                    userView.unlockSession(Model.unlock_session("none", intent.getStringExtra("code")))
                }
            }

        }

    }

    override fun showMonsterDialog() {
        fullscreenMonsterDialog = FullscreenDialogWithAnimation()

        fullscreenMonsterDialog.show(supportFragmentManager.beginTransaction(), FullscreenDialogWithAnimation.TAG)
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
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemThatWasClickedId = item.getItemId()
        when (itemThatWasClickedId) {
            R.id.settings -> {
                val preferenceFragment = SettingsFragment()
                supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.session_container, preferenceFragment)
                    .addToBackStack("ROOT")
                    .commit()
                return true
            }
            R.id.logout -> {
                userView.userRepo.nukeUsers()
                getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                    .edit()
                    .remove(getString(R.string.userIdKey))
                    .remove(getString(R.string.authTokenKey))
                    .apply()
                navigation.visibility = View.GONE
                loginFragment = LoginFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, loginFragment)
                    .commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Settings Management
    override fun onPreferenceClick(fragmentType: FragmentType) {
        when (fragmentType) {
            FragmentType.GROUP -> {
                groupFragment = GroupFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, groupFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
            FragmentType.EMAIL -> {
                emailFragmentFragment = ChangeEmailSettingsFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, emailFragmentFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
            FragmentType.PASSWORD -> {
                passwordFragment = ChangePasswordFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, passwordFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
            FragmentType.EULA -> {
                EULAFragment = EULAFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, EULAFragment)
                    .addToBackStack(null)
                    .setTransition(R.anim.slide_up)
                    .commit()
            }
        }
    }

     fun setActionBarTitle(title: String) {
         this.supportActionBar?.title = title
     }

    fun showAcionBar(bool : Boolean) {
        if (bool) {
            this.supportActionBar?.show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

            }
        } else {
            this.supportActionBar?.hide()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.parseColor("#A3A29F")

            }
        }
    }
}
