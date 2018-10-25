package com.hogent.mindfulness.show_sessions

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import com.hogent.mindfulness.exercises_List_display.ExercisesListFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.session_fragment.*
import org.jetbrains.anko.support.v4.act

class SessionFragment(): Fragment(),
        SessionAdapter.SessionAdapterOnClickHandler{

    private lateinit var adapter: RecyclerView.Adapter<SessionAdapter.SessionViewHolder>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sessions: Array<Model.Session>


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



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
    }

    override fun onResume(){
        super.onResume()
        val viewAdapter = SessionAdapter(sessions, this)
        val viewManager = GridLayoutManager(activity, 2)

        rv_sessions.apply{
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onClick(session: Model.Session) {
        beginRetrieveExercises(session._id)
        Log.d("test", "onclick")
        Toast.makeText(activity, session.title, Toast.LENGTH_SHORT).show()

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




    }

    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }


}