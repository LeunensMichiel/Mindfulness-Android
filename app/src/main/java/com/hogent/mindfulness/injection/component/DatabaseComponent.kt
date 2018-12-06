package com.hogent.mindfulness.injection.component

import com.hogent.mindfulness.injection.App
import com.hogent.mindfulness.injection.module.DatabaseModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
interface DatabaseComponent {
    fun inject(app: App)
    fun inject(userViewModel: UserViewModel)
}