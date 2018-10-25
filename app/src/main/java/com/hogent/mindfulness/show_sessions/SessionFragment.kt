package com.hogent.mindfulness.show_sessions


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hogent.mindfulness.R
import com.hogent.mindfulness.domain.Model
import kotlinx.android.synthetic.main.session_fragment.*


class SessionFragment() : Fragment() {


    lateinit var sessions: Array<Model.Session>
    lateinit var ac: SessionAdapter.SessionAdapterOnClickHandler



    // I used this resource: https://developer.android.com/guide/topics/ui/layout/recyclerview
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.session_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter = SessionAdapter(sessions, ac)
        val viewManager = GridLayoutManager(activity, 2)



        rv_sessions.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }





}