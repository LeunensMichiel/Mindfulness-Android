package com.hogent.mindfulness.exercises_List_display


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model.Exercise
import kotlinx.android.synthetic.main.fragment_exercises_pane.*

class ExercisesListFragment : Fragment(),
    ExerciseAdapter.ExerciseAdapterOnClickHandler {

    private lateinit var viewAdapter: RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

    override fun onResume() {
        super.onResume()
        viewAdapter = ExerciseAdapter(mExercisesList, this)
        viewManager = LinearLayoutManager(activity)

        rv_exercises.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onClick(exercise: Exercise) {
        Log.d("test", "onclick")
        Toast.makeText(activity, exercise.title, Toast.LENGTH_SHORT).show()
    }

}
