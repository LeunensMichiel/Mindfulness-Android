//package com.hogent.mindfulness.data
//
//import com.hogent.mindfulness.domain.Model
//import io.reactivex.Single
//
//class UserRepository  constructor(private val database: AppDatabase){
//
//    fun getUser(userId: String): Single<Model.User> {
//        return database.userDao().findById(userId)
//    }
//
//    fun insertUser(user: Model.User) {
//        database.userDao().insert(user)
//    }
//
//
////    companion object {
////        @Volatile private var instance: UserRepository? = null
////
////        fun getInstance(userDao: UserDao) =
////                instance?: synchronized(this){
////                    instance?: UserRepository(userDao).also { instance = it }
////                }
////    }
//}