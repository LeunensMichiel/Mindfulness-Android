package com.hogent.mindfulness
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
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
import com.hogent.mindfulness.login.ForgotPasswordFragment
import com.hogent.mindfulness.login.LoginFragment
import com.hogent.mindfulness.login.RegisterFragment
import com.hogent.mindfulness.post.PostFragment
import com.hogent.mindfulness.profile.ProfileFragment
import com.hogent.mindfulness.services.NotifyJobCreator
import com.hogent.mindfulness.services.SingleJob
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
    private lateinit var loginFragment: LoginFragment
    private lateinit var registerFragment: RegisterFragment
    private lateinit var forgotPasswordFragment: ForgotPasswordFragment
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
    private lateinit var userView: UserViewModel
    private lateinit var sessionView: SessionViewModel
    private lateinit var exView: ExerciseViewModel
    private lateinit var pageView: PageViewModel
    private lateinit var stateView: StateViewModel
    private lateinit var postView: PostViewModel
    private var currentPost = Model.Post()

    /**
     * Set view to MainActivity
     * Sets bottom Navigation
     * Initializes ViewModels (all of them)
     * Observes all the viewmodels so that the right fragments get loaded into view and data gets initialized
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

        userView.loggingIn = true

        /**
         * Is used to update the Fragments for the Exercises and Pages
         */
        stateView.viewState.observe(this, Observer {
            when (it!!) {
                "EXERCISE_VIEW" -> {
                    if (!::exerciseFragment.isInitialized) {
                        exerciseFragment = ExercisesListFragment()
                    }
                    //sessionView.loadImages()
                    exerciseFragment.session = sessionView.selectedSession.value!!
                    currentPost.session_name = sessionView.selectedSession.value!!.title
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, exerciseFragment)
                        .addToBackStack("tag")
                        .commit()
                }
                "PAGE_VIEW" -> {
                    pageView.retrievePages()
                    exerciseDetailFragment = ExerciseDetailFragment()
                    exerciseDetailFragment.manager = supportFragmentManager
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, exerciseDetailFragment)
                        .addToBackStack("tag")
                        .commit()
                }
            }
        })

        /**
         * Displays a dialog so the user can give feedback about a session or can unsusbcribe from notifications about feedback
         */
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

        /**
         * Checks if the applications has a user. and updates the UI accordingly and will also initialize the notifications if the app has a user
         * If the user doesnt have a group, it will be redirected to the groupfragment, otherwhise sessionFragment Will load
         */
        userView.dbUser.observe(this, Observer<Model.User?> {
            if (it == null) {
                navigation.visibility = View.GONE
                loginFragment = LoginFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, loginFragment)
                    .commit()
                showAcionBar(false)

            } else {
                if (it.group != null && userView.loggingIn) {
                    showAcionBar(true)
                    sessionView.resetunlockedSession()
                    navigation.visibility = View.VISIBLE
                    val notifs = userView.dbUser.value!!.group!!.notifications
                    if (notifs != null) {
                        notifs?.let {
                            for (i in it) {
                                SingleJob.scheduleJob(
                                    (i.notification_launchtijdstip.hours * 60) + i.notification_launchtijdstip.minutes,
                                    TimeUnit.HOURS.toMillis(24),
                                    i.notification_title,
                                    i.notification_beschrijving,
                                    "mindfulness",
                                    "notif${i._id}"
                                )
                            }
                        }
                    }
                    sessionFragment = SessionFragment()

                    postFragment = PostFragment()
                    profileFragment = ProfileFragment()
                    userView.retrieveProfilePicture()
                    toSessions()
                } else if (it.group == null) {
                    navigation.visibility = View.GONE

                    groupFragment = GroupFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.session_container, groupFragment)
                        .commit()
                    showAcionBar(true)

                }
            }

        })

        /**
         * Will observe for a toastmessage and shall display it
         */
        userView.toastMessage.observe(this, Observer {
            if (it != null)
                toast(it).show()
        })

        /**
         * Retrieves the exercises if a sessions is selected and observes it
         */
        sessionView.selectedSession.observe(this, Observer<Model.Session> {
            if (it != null) {
                if (it.unlocked) {
                    exView.session_id = it._id
                    exView.session_image_filename = it.imageFilename
                    exView.retrieveExercises()
                    exView.retrieveExerciseImg(it.imageFilename)
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

        /**
         * Observes the selectedExercise so the ui changes accordingly
         */
        exView.selectedExercise.observe(this, Observer {
            if (it != null) {
                pageView.exercise_id = it._id
                stateView.viewState?.value = "PAGE_VIEW"
                pageView.ex_name = it.title
            }
        })

        exView.toastMessage.observe(this, Observer {
            if (it != null){
                toast(it).show()
            }
        })

        pageView.pageError.observe(this, Observer {
            if (it != null && !it.equals(Model.errorMessage())) {
                toast(it.error).show()
            }
        })

        postView.postError.observe(this, Observer {
            if (it != null ){
                toast(it).show()
            }
        })

        postView.error.observe(this, Observer {
            if (it != null && !it.equals(Model.errorMessage())) {
                toast(it.error).show()
            }
        })

        /**
         * This wil create a JobManager so that we can have notifications
         */
        // Starts van notification service
        JobManager.create(this).addJobCreator(NotifyJobCreator())

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    /**
     * This method checks if an Intent has an extra called sessionID. If so, the mainactivity uses it to show a Feedback Screen
     * @param newIntent is the intent we recieve
     */
    override fun onNewIntent(newIntent: Intent) {
        this.intent = newIntent

        if (intent.hasExtra("sessionID")) {
            sessionView.setSession(intent.getStringExtra("sessionID"))
            stateView.dialogState?.value = "FEEDBACK_DIALOG"
        }
    }

    /**
     *     This function starts the session Fragment

     */
    fun toSessions() {
        sessionFragment = SessionFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, sessionFragment)
            .commit()
    }

    /**
     *  This function replaces the register or forgotPassword fragment back with the login fragment
     */
    fun toLogin(v: View) {
        loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, loginFragment)
            .commit()
    }

    /**
     * This function replaces the login fragment with the register fragment
     */
    fun toRegister(v: View) {
        registerFragment = RegisterFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, registerFragment)
            .commit()
    }
    /**
     * This function replaces the login fragment with the forgotPassword fragment
     */
    fun toForgot(v : View) {
        forgotPasswordFragment = ForgotPasswordFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, forgotPasswordFragment)
            .commit()
    }

    /**
     * Checks when the app restarts, a uses has given a group code.
     * If the app was exited straight after pressing register, this makes sure the app doesnt crash due to a groupless user
     */
    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("code")) {
            if (intent.hasExtra("register")) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.session_container, RegisterFragment())
                    .commit()
            } else if (userView.userRepo.user.value?.group == null) {
                userView.addGroup(Model.user_group(intent.getStringExtra("code")))
            } else {
                if (userView.userRepo.user.value?.unlocked_sessions!!.contains(intent.getStringExtra("code"))) {
                    Toast.makeText(this, "Sessie reeds vrijgespeeld", Toast.LENGTH_SHORT).show()
                } else {
                    userView.unlockSession(Model.unlock_session("none", intent.getStringExtra("code")))
                }
            }
        }
    }

    /**
     * This method displays a Dialog to congratulate the user on unlocking a session and thus getting a new monster
     */
    override fun showMonsterDialog() {
        fullscreenMonsterDialog = FullscreenDialogWithAnimation()
        fullscreenMonsterDialog.show(supportFragmentManager.beginTransaction(), FullscreenDialogWithAnimation.TAG)
    }

    /**
     * Initialize NavigationListener
     * Specify Fragment to add to Activity via itemId in Navigation
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (::exerciseDetailFragment.isInitialized)
            supportFragmentManager.beginTransaction().remove(exerciseDetailFragment).commit()
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

    /**
     * Pops the supportFragmentManager to get to the previous fragment
     */
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    /**
     * This creates an option Menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    /**
     * Checks what the user has clicked in the optionMenu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemThatWasClickedId = item.getItemId()
        when (itemThatWasClickedId) {
            R.id.settings -> {
                toSettings()
                return true
            }
            R.id.logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * This will bring the user to the settingsFragment
     */
    fun toSettings() {
        val preferenceFragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.session_container, preferenceFragment)
            .addToBackStack("ROOT")
            .commit()
    }
    /**
     * This will log the user out and make sure the sharedPreferences are set correctly so the app gets closed correctly
     */
    fun logout() {
        userView.loggingIn = true
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
    }

    /**
     * This is a function that checks which Custom Preference Fragment has been clicked on and redirects the user
     */
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

    /**
     * Accessable method to change the actionBarTitle from a fragment
     * @param title the title for the actionbar
     */
    fun setActionBarTitle(title: String) {
        this.supportActionBar?.title = title
    }

    /**
     *  Hides or shows the Actionbar for if the user is logged in or not
     *  @param bool will change it accordingly
     */
    fun showAcionBar(bool: Boolean) {
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
