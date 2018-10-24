package com.hogent.mindfulness

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.hogent.mindfulness.domain.Session
import com.hogent.mindfulness.show_sessions.SessionAdapter


class MainActivity : AppCompatActivity(),
    SessionAdapter.SessionAdapterOnClickHandler {

    /*val ApiService by lazy {
        ApiService.create()
    }*/

    private val data = arrayOf(
        Session("Session 1", 1),
        Session("Session 2", 2),
        Session("Session 3", 3),
        Session("Session 4", 4),
        Session("Session 5", 5),
        Session("Session 6", 6),
        Session("Session 7", 7),
        Session("Session 8", 8)
    )

    private lateinit var mAdapter: SessionAdapter
    private lateinit var mSessionList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mSessionList = findViewById(R.id.rv_numbers)

        val layoutManager = GridLayoutManager(this, 2)
        mSessionList.setLayoutManager(layoutManager)

        mSessionList.setHasFixedSize(true)

        mAdapter = SessionAdapter(data, this)
        mSessionList.setAdapter(mAdapter)
    }

    override fun onClick(session: Session) {
        Log.d("test", "onclick")
        Toast.makeText(this, session.title, Toast.LENGTH_SHORT).show()
    }
}
