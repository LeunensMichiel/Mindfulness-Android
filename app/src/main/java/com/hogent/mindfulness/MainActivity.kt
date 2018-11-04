package com.hogent.mindfulness

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExerciseAdapter
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.login.LoginActivity
import com.hogent.mindfulness.oefeningdetails.*
import com.hogent.mindfulness.show_sessions.SessionAdapter
import com.hogent.mindfulness.show_sessions.SessionFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_paragraaf_tekst.*


class MainActivity : AppCompatActivity(), SessionAdapter.SessionAdapterOnClickHandler, ExerciseAdapter.ExerciseAdapterOnClickHandler {



    private lateinit var disposable: Disposable
    private lateinit var sessionFragment: SessionFragment
    private lateinit var exerciseFragment: ExercisesListFragment
    private lateinit var oefeningDetailFragment: OefeningDetailFragment

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }


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
        }
        false
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
