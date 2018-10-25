package com.hogent.mindfulness

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.hogent.mindfulness.R.id.rv_numbers
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import com.hogent.mindfulness.show_sessions.SessionAdapter
import com.hogent.mindfulness.show_sessions.SessionFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SessionAdapter.SessionAdapterOnClickHandler {
    override fun onClick(session: Model.Session) {
        beginRetrieveExercises(session._id)
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
        val fragment = ExercisesListFragment()

        fragment.mExercisesList = exercises

        supportFragmentManager.beginTransaction()
            .add(R.id.rv_exercises, fragment)
            .commit()
    }

    private lateinit var disposable: Disposable

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragment = SessionFragment()

        this.supportFragmentManager.beginTransaction()
            .add(R.id.session_container, fragment)
            .commit()

    }


    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }

}
