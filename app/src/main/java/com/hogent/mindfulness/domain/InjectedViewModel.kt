package com.hogent.mindfulness.domain

import android.arch.lifecycle.ViewModel
import com.hogent.mindfulness.domain.ViewModels.*
import com.hogent.mindfulness.injection.App

abstract class InjectedViewModel:ViewModel() {

    init {
        inject()
    }

    private fun inject() {
        when(this) {
            is UserViewModel -> App.component.inject(this)
            is SessionViewModel -> App.component.inject(this)
            is ExerciseViewModel -> App.component.inject(this)
            is PageViewModel -> App.component.inject(this)
            is PostViewModel -> App.component.inject(this)
        }
    }
}