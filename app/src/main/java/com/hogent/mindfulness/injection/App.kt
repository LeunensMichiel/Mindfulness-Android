package com.hogent.mindfulness.injection

import android.app.Application
import com.hogent.mindfulness.injection.component.DaggerViewModelComponent
import com.hogent.mindfulness.injection.component.ViewModelComponent
import com.hogent.mindfulness.injection.module.DatabaseModule
import com.hogent.mindfulness.injection.module.NetworkModule

class App:Application() {

    companion object {
        lateinit var component:ViewModelComponent
    }


    override fun onCreate() {
        super.onCreate()
        component = DaggerViewModelComponent
            .builder()
            .databaseModule(DatabaseModule(this))
            .networkModule(NetworkModule())
            .build()
    }
}