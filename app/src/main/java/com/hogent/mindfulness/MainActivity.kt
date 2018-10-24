package com.hogent.mindfulness

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.show_sessions.SessionAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
    SessionAdapter.SessionAdapterOnClickHandler {

    private lateinit var disposable: Disposable

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        beginRetrieveSessionmap()

    }

    private fun beginRetrieveSessionmap() {
        disposable = mindfulnessApiService.getSessionmap(getString(R.string.sessionmap_id))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result -> showResult(result)},
                {error -> showError(error.message)}
            )
    }

    private fun showResult(sessionmap: Model.Sessionmap) {

        val sessionLayoutManager = GridLayoutManager(this, 2)
        val sessionAdapter = SessionAdapter(sessionmap.sessions, this)

        rv_numbers.apply{
            layoutManager = sessionLayoutManager
            setHasFixedSize(false)
            adapter = sessionAdapter
        }



    }

    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }


    override fun onClick(session: Model.Session) {
        val fragment = ExercisesListFragment()
        fragment.mExercisesList = session.exercises

        supportFragmentManager .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
        /*Log.d("test", "onclick")
        Toast.makeText(this, session.title, Toast.LENGTH_SHORT).show()*/
    }
}
