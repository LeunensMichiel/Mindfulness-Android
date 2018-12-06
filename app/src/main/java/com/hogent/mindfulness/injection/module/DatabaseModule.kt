package com.hogent.mindfulness.injection.module

import android.app.Application
import android.content.Context
import com.hogent.mindfulness.data.LocalDatabase.MindfullDatabase
import com.hogent.mindfulness.data.LocalDatabase.dao.UserDao
import com.hogent.mindfulness.data.LocalDatabase.repository.UserRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun provideMindfullRepository(userDao: UserDao):UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    internal fun provideUserDao(mindfullDatabase: MindfullDatabase): UserDao {
        return mindfullDatabase.userDao()
    }

    @Provides
    @Singleton
    internal fun provideWordDatabase(context: Context): MindfullDatabase {
        return MindfullDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return application
    }

}