package com.hogent.mindfulness.exercises_List_display

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model.Exercise
import kotlinx.android.synthetic.main.exercise_list_item.view.*

class ExerciseAdapter(
    // This array has the data for the recyclerview adapter
    private val mExercisesData: Array<Exercise>,
    //mClickHandler is for communicating whit the activity when item clicked
    private val mClickHandler: ExerciseAdapterOnClickHandler
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // This function loads in the item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val layoutIdExListItem = R.layout.exercise_list_item

        val view = inflater.inflate(layoutIdExListItem, parent, false)

        return ExerciseViewHolder(view)
    }
    //  This function gives the size back of the data list
    override fun getItemCount(): Int {
        return mExercisesData.size
    }

    //  This function attaches the data to item view
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exerciseTitle = mExercisesData[position]
        holder.title.text = exerciseTitle.title
    }

    inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.tv_exercise_title

        // Add clicklistener on the item from the recyclerview
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            // Get the correct exercise out of the data array
            val adapterPosition = adapterPosition
            val exercise = mExercisesData[adapterPosition]

            mClickHandler.onClickExercise(exercise)

        }
    }

    // Implement this interface for passing click event through
    interface ExerciseAdapterOnClickHandler {
        fun onClickExercise(exercise: Exercise)
    }

}