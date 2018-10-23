package com.hogent.mindfulness.exercises_display.exercises_List_display

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hogent.mindfulness.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager .beginTransaction()
            .add(R.id.fragment_container, ExerciseslistFragment())
            .commit()
    }
}
