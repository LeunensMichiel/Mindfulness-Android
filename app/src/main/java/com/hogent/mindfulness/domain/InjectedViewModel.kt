package com.hogent.mindfulness.domain

import android.arch.lifecycle.ViewModel
import com.hogent.mindfulness.injection.App
import com.hogent.mindfulness.injection.component.DaggerViewModelComponent
import com.hogent.mindfulness.injection.component.ViewModelComponent

abstract class InjectedViewModel:ViewModel() {



    init {
        inject()
    }

    private fun inject() {
        when(this) {
            is UserViewModel -> App.component.inject(this)
        }
    }
}