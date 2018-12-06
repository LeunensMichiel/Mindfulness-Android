package com.hogent.mindfulness.data.LocalDatabase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.hogent.mindfulness.domain.Model

@Dao
interface UserDao {

    @Query("SELECT * from user_table")
    fun getUser():LiveData<Model.User>

    @Insert
    fun insert(user:Model.User)
}