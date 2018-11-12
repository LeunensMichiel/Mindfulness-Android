package com.hogent.mindfulness.exercises_List_display


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.ExerciseApiService
import com.hogent.mindfulness.data.ServiceGenerator
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.exercise_list_item.view.*
import kotlinx.android.synthetic.main.fragment_exercises_pane.*

class ExercisesListFragment : Fragment() {

    /**
     * Here will the exercisesData be stored
     *This will be used the populate the data for the ExerciseAdapter
     */
    lateinit var session: Model.Session
    private lateinit var disposable: Disposable

    /**
     * I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
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
        val exerciseApiService = ServiceGenerator.createService(ExerciseApiService::class.java)

        disposable = exerciseApiService.getExercises(session_id)
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
        val viewManager = LinearLayoutManager(activity)

        rv_exercises.apply {

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    /***********************************************************************************************
     * Adapter
     *
     ***********************************************************************************************/

    class ExerciseAdapter(
        // This array has the data for the recyclerview adapter
        private val mExercisesData: Array<Model.Exercise>,
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

        inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: Button = view.btn_exercise

            // Add clicklistener on the item from the recyclerview
            init {
                title.setOnClickListener {
                    // Get the correct exercise out of the data array
                    val adapterPosition = adapterPosition
                    val exercise = mExercisesData[adapterPosition]

                    mClickHandler.onClickExercise(exercise)
                }
            }

        }

        // Implement this interface for passing click event through
        interface ExerciseAdapterOnClickHandler {
            fun onClickExercise(exercise: Model.Exercise)
        }

    }

}
