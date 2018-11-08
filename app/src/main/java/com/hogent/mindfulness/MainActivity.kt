package com.hogent.mindfulness

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.login.LoginActivity
import com.hogent.mindfulness.oefeningdetails.*
import com.hogent.mindfulness.scanner.ScannerActivity
import com.hogent.mindfulness.show_sessions.SessionFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_exercises_pane.*
import kotlinx.android.synthetic.main.session_fragment.*


class MainActivity : AppCompatActivity(), SessionFragment.SessionAdapter.SessionAdapterOnClickHandler, ExercisesListFragment.ExerciseAdapter.ExerciseAdapterOnClickHandler {


    //initializing attributes
    private lateinit var disposable: Disposable
    private lateinit var sessionFragment: SessionFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var oefeningDetailFragment: OefeningDetailFragment


    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    /**
     * Set view to MainActivity
     * Set ItemSelectedListener for the navigation
     * initialize SessionFragment
     * add SessionFragment to activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (checkIfLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        sessionFragment = SessionFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.session_container, sessionFragment)
            .commit()

    }

    private fun checkIfLoggedIn(): Boolean {
        val token = getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
            .getString(getString(R.string.authTokenKey), null)
        return token == null
    }



    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("code")){
            Log.d("code", intent.getStringExtra("code"))
            val sharedPref = getSharedPreferences(getString(R.string.sharedPreferenceUserDetailsKey), Context.MODE_PRIVATE)
                .getString(getString(R.string.userIdKey), "")
            Log.d("user", sharedPref)
            val unlock_session = Model.unlock_session(sharedPref, intent.getStringExtra("code"))
            disposable = mindfulnessApiService.updateUser(unlock_session)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> showResult(result) },
                    { error -> showError(error.message) }
                )
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

        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, exerciseFragment)
            .addToBackStack("tag")
            .commit()
    }

    /**
     * Initialize OefeningDetailFragment
     * Initialize manager in oefeningDetailFragment
     * Initialize exerciseId in oefeningDetailFragment
     * Add OefeningDetailFragment to Activity
     */
    override fun onClickExercise(exercise: Model.Exercise) {
        oefeningDetailFragment = OefeningDetailFragment()

        oefeningDetailFragment.manager = supportFragmentManager
        oefeningDetailFragment.exerciseId = exercise._id

        supportFragmentManager.beginTransaction()
            .replace(R.id.session_container, oefeningDetailFragment)
            .addToBackStack("tag")
            .commit()
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
