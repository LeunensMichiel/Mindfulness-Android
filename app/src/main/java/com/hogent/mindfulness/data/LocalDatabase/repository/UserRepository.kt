package com.hogent.mindfulness.data.LocalDatabase.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import com.hogent.mindfulness.data.LocalDatabase.dao.UserDao
import com.hogent.mindfulness.domain.Model
import org.jetbrains.anko.doAsync

class UserRepository(private val userDao: UserDao) {
    var user: LiveData<Model.User> = userDao.getUser()

    @WorkerThread
    fun insert(user: Model.User) {
        doAsync {
            userDao.insert(user)
        }
    }

    @WorkerThread
    fun nukeUsers() {
        doAsync {
            userDao.nukeUsers()
        }
    }

    @WorkerThread
    fun updateUser(user: Model.User) {
        doAsync {
            userDao.updateUser(user)
        }
    }
}