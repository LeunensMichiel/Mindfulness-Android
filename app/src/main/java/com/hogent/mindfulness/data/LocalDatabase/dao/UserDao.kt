package com.hogent.mindfulness.data.LocalDatabase.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.hogent.mindfulness.domain.Model

@Dao
interface UserDao {

    @Query("SELECT * from user_table ORDER BY _id LIMIT 1")
    fun getUser():LiveData<Model.User>

    @Insert(onConflict = REPLACE)
    fun insert(user:Model.User)

    @Query("DELETE FROM user_table")
    fun nukeUsers()
}