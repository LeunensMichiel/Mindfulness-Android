package com.hogent.mindfulness.injection.component

import com.hogent.mindfulness.injection.module.NetworkModule
import dagger.Component
import javax.inject.Singleton


@Singleton
/**
 * All modules that are required to perform the injections into the listed objects should be listed
 * in this annotation
 */
@Component(modules = [NetworkModule::class])
interface NetworkComponent {
    fun inject(userViewModel: UserViewModel)
}