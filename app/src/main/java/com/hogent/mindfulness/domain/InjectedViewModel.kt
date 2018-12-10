package com.hogent.mindfulness.domain

import android.arch.lifecycle.ViewModel
import com.hogent.mindfulness.domain.ViewModels.SessionViewModel
import com.hogent.mindfulness.domain.ViewModels.UserViewModel
import com.hogent.mindfulness.injection.App

abstract class InjectedViewModel:ViewModel() {



    init {
        inject()
    }

    private fun inject() {
        when(this) {
            is UserViewModel -> App.component.inject(this)
            is SessionViewModel -> App.component.inject(this)
        }
    }
}