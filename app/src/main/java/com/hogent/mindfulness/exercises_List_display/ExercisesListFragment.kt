package com.hogent.mindfulness.exercises_List_display


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model.Exercise
import com.hogent.mindfulness.oefeningdetails.OefeningDetailsActivity
import kotlinx.android.synthetic.main.fragment_exercises_pane.*

class ExercisesListFragment : Fragment(),
    ExerciseAdapter.ExerciseAdapterOnClickHandler {

    // Here will the exercisesData be stored
    // This will be used the populate the data for the ExerciseAdapter
    lateinit var mExercisesList: Array<Exercise>

    // I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercises_pane, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter = ExerciseAdapter(mExercisesList, this)
        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        rv_exercises.apply {

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onClick(exercise: Exercise) {
        // This intent launches the detail activity of exercise
        val intent = Intent(activity, OefeningDetailsActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, exercise._id)
        startActivity(intent)
    }





}