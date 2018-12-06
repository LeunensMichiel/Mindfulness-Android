package com.hogent.mindfulness.injection

import android.app.Application
import com.hogent.mindfulness.injection.component.DaggerDatabaseComponent
import com.hogent.mindfulness.injection.component.DatabaseComponent
import com.hogent.mindfulness.injection.module.DatabaseModule

class App:Application() {

    companion object {
        lateinit var component:DatabaseComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerDatabaseComponent
            .builder()
            .databaseModule(DatabaseModule(this))
            .build()
    }
}