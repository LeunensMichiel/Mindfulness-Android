package com.hogent.mindfulness.show_sessions


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.data.MindfulnessApiService
import com.hogent.mindfulness.domain.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.session_fragment.*


class SessionFragment() : Fragment() {


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
        beginRetrieveSessionmap()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
    }


    //this function retrieves a sessionmap from the database
    private fun beginRetrieveSessionmap() {
        disposable = mindfulnessApiService.getSessionmap(getString(R.string.sessionmap_id))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> showResult(result) },
                { error -> showError(error.message) }
            )
    }

    private fun showError(errMsg: String?) {
//        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }

    private fun showResult(sessions:Array<Model.Session>){
        val viewAdapter = SessionAdapter(sessions, activity as SessionAdapter.SessionAdapterOnClickHandler)
        val viewManager = GridLayoutManager(activity, 2)



        rv_sessions.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


}