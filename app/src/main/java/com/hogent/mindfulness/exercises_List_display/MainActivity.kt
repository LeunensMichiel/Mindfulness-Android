package com.hogent.mindfulness.exercises_List_display

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.Model
import com.hogent.mindfulness.data.MindfulnessApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ExerciseList.*

class MainActivity : AppCompatActivity() {

    private lateinit var disposable: Disposable

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ExerciseList)

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

        Log.d("sessionmap", sessionmap.sessions.size.toString())
        tv_session_title.text = sessionmap.sessions.get(0).title

        val fragment = ExercisesListFragment()
        fragment.mExercisesList = sessionmap.sessions.get(0).exercises

        supportFragmentManager .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showError(errMsg: String?) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }
}
