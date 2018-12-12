package com.hogent.mindfulness.data.LocalDatabase.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import android.util.Log
import com.hogent.mindfulness.data.LocalDatabase.dao.UserDao
import com.hogent.mindfulness.domain.Model

class UserRepository(private val userDao: UserDao) {
    var user: LiveData<Model.User> = userDao.getUser()

    @WorkerThread
    fun insert(user:Model.User){
        userDao.insert(user)
    }

    @WorkerThread
    fun nukeUsers(){
        userDao.nukeUsers()
    }

    @WorkerThread
    fun updateUser(user:Model.User){
        userDao.updateUser(user)
    }
}