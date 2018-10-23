package com.hogent.mindfulness.exercises_display.exercises_List_display


import android.app.Activity
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
import com.hogent.mindfulness.exercises_display.Model.Exercise
import kotlinx.android.synthetic.main.fragment_exercises_pane.*

class ExerciseslistFragment : Fragment(), ExerciseAdapter.ExerciseAdapterOnClickHandler {


    private lateinit var viewAdapter: RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager


    // I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_exercises_pane, container, false)
    }

    override fun onResume() {
        super.onResume()
        val data = arrayListOf<Exercise>()

        data.addAll(listOf(Exercise(3,3,"qdfqsddd",3),
            Exercise(3,3,"mqsddddqqqjfmqs",3), Exercise(3,3,"mqsdjfqddqfqsldfmqs",3)))

        viewAdapter = ExerciseAdapter(data, this)
        rv_exercises.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }
    }

    override fun onClick(exercise: Exercise) {
        Log.d("test", "onclick")
        Toast.makeText(activity, exercise.title, Toast.LENGTH_SHORT).show()
    }

}
