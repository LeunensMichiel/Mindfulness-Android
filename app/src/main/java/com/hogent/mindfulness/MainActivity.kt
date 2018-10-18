package com.hogent.mindfulness

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    /*val ApiService by lazy {
        ApiService.create()
    }*/

    private val NUM_LIST_ITEMS = 100
    private lateinit var mAdapter: SessieAdapter
    private lateinit var  mNumbersList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNumbersList = findViewById(R.id.rv_numbers)

        val layoutManager = LinearLayoutManager(this)
        mNumbersList.setLayoutManager(layoutManager)

        mNumbersList.setHasFixedSize(true)

        mAdapter = SessieAdapter(NUM_LIST_ITEMS)
        mNumbersList.setAdapter(mAdapter)
    }



}
