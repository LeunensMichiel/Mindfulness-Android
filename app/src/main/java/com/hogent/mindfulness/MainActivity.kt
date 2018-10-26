package com.hogent.mindfulness

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.show_sessions.SessionAdapter
import com.hogent.mindfulness.show_sessions.SessionFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity(), SessionAdapter.SessionAdapterOnClickHandler {


    private lateinit var disposable: Disposable
    private lateinit var sessionFragment: SessionFragment
    private lateinit var exerciseFragment: ExercisesListFragment

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
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showResult(sessionmaps: Model.Sessionmap) {
        sessionFragment = SessionFragment()

        sessionFragment.sessions = sessionmaps.sessions
        sessionFragment.ac = this

        supportFragmentManager.beginTransaction()
            .add(R.id.session_container, sessionFragment)
            .commit()


    }

    private fun beginRetrieveExercises(session_id: String) {
        disposable = mindfulnessApiService.getExercises(session_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResultExercises(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showResultExercises(exercises: Array<Model.Exercise>) {

        exerciseFragment = ExercisesListFragment()

        exerciseFragment.mExercisesList = exercises

        supportFragmentManager.beginTransaction()
            .remove(sessionFragment).add(R.id.session_container, exerciseFragment)
            .commit()

    }

    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }


    override fun onClick(session: Model.Session) {
        beginRetrieveExercises(session._id)
    }

}
