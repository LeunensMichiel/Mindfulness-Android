//package com.hogent.mindfulness.data
//
//import android.arch.persistence.room.Dao
//import android.arch.persistence.room.Insert
//import android.arch.persistence.room.Query
//import com.hogent.mindfulness.domain.Model
//import io.reactivex.Single
//
//@Dao
//interface UserDao {
//    @Insert
//    fun insert(vararg user: Model.User)
//
//    @Query("SELECT * FROM user WHERE _id = :id")
//    fun findById(id: String): Single<Model.User>
//}