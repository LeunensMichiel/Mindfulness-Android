package com.hogent.mindfulness.exercises_List_display


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.domain.ViewModels.ExerciseViewModel
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.hogent.mindfulness.services.SingleJob
import kotlinx.android.synthetic.main.exercise_list_item.view.*
import kotlinx.android.synthetic.main.fragment_exercises_pane.*
import org.jetbrains.anko.imageBitmap

class ExercisesListFragment : Fragment() {

    /**
     * Here will the exercisesData be stored
     *This will be used the populate the data for the ExerciseAdapter
     */
    lateinit var session: Model.Session
    private lateinit var exView: ExerciseViewModel
    private lateinit var userViewModel: UserViewModel

    /**
     * I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        exView = activity?.run {
            ViewModelProviders.of(this).get(ExerciseViewModel::class.java)
        }?: throw Exception("Invalid activity")

        userViewModel = activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        SingleJob.scheduleJob(
            1,
            30,
            "Mindfulness",
            "Wat was je ervaring met " + session.title + "?",
            "mindfulness",
            session._id,
            session._id
        )

        exView.retrieveExercises()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercises_pane, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseSessiontitle.text = session.title
        sessionDescriptionExercise.text = session.description

        val viewAdapter = ExerciseAdapter(this, exView)
        val viewManager = LinearLayoutManager(activity)

        rv_exercises.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(session.title)

    }

    /***********************************************************************************************
     * Adapter
     *
     ***********************************************************************************************/
    class ExerciseAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val exView:ExerciseViewModel
    ) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

        private var mExercisesData: Array<Model.Exercise> = arrayOf()
        private var sessionBitmap: Bitmap? = null

        init {
            if (exView.sessionBitmap.value != null) {
                sessionBitmap = exView.sessionBitmap.value
            }
            exView.sessionBitmap.observe(lifecycleOwner, Observer {
                if (it != null){
                    sessionBitmap = it
                    this.notifyDataSetChanged()
                }
            })
            exView.exercises.observe(lifecycleOwner, Observer {
                mExercisesData = it!!
                this.notifyDataSetChanged()
            })
        }

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
            if (sessionBitmap != null){
                holder.image.imageBitmap = sessionBitmap
            }
        }

        inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.exerciseName
            val card: CardView = view.btn_exercise
            var image: ImageView = view.exerciseImage

            // Add clicklistener on the item from the recyclerview
            init {
                card.setOnClickListener {
                    // Get the correct exercise out of the data array
                    val adapterPosition = adapterPosition
                    exView.selectedExercise?.value = mExercisesData[adapterPosition]

                }
            }
        }
    }

}
