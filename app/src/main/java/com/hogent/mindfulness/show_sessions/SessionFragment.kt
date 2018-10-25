package com.hogent.mindfulness.show_sessions

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hogent.mindfulness.R
import com.hogent.mindfulness.R.id.rv_numbers
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SessionFragment(): Fragment()
    , SessionAdapter.SessionAdapterOnClickHandler {

    private lateinit var disposable: Disposable
    private lateinit var adapter: RecyclerView.Adapter<SessionAdapter.SessionViewHolder>
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val mindfulnessApiService by lazy {
        MindfulnessApiService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        beginRetrieveSessionmap()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
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

        val sessionLayoutManager = GridLayoutManager(activity, 2)
        val sessionAdapter = SessionAdapter(sessionmap.sessions, this)

        R.id.rv_numbers.apply {
            layoutManager = sessionLayoutManager
            adapter = sessionAdapter

        }
    }


    private fun showError(errMsg: String?) {
        Toast.makeText(activity, errMsg, Toast.LENGTH_SHORT).show()
    }


    override fun onClick(session: Model.Session) {
        /*val fragment = ExercisesListFragment()

        fragment.mExercisesList = session.exercises

        if(expanded)
        {


            expanded = false
        }else{

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
            expanded = true
        }*/





        /*Log.d("test", "onclick")
        Toast.makeText(this, session.title, Toast.LENGTH_SHORT).show()*/
    }
}