package com.hogent.mindfulness.injection.component

import com.hogent.mindfulness.domain.ViewModels.*
import com.hogent.mindfulness.injection.App
import com.hogent.mindfulness.injection.module.DatabaseModule
import com.hogent.mindfulness.injection.module.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface ViewModelComponent {
    fun inject(app:App)
    fun inject(userViewModel: UserViewModel)
    fun inject(sessionViewModel: SessionViewModel)
    fun inject(exerciseViewModel: ExerciseViewModel)
    fun inject(pageViewModel: PageViewModel)
    fun inject(postViewModel: PostViewModel)
}