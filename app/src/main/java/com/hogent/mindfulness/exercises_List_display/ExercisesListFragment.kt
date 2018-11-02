package com.hogent.mindfulness.exercises_List_display


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_exercises_pane.*

class ExercisesListFragment : Fragment() {

    // Here will the exercisesData be stored
    // This will be used the populate the data for the ExerciseAdapter
    lateinit var session : Model.Session
    private lateinit var disposable: Disposable
    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    // I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beginRetrieveExercises(session._id)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercises_pane, container, false)
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

    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }

    private fun showResultExercises(exercises: Array<Model.Exercise>) {

        val viewAdapter = ExerciseAdapter(exercises, activity as ExerciseAdapter.ExerciseAdapterOnClickHandler)
        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        rv_exercises.apply {

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }





}
