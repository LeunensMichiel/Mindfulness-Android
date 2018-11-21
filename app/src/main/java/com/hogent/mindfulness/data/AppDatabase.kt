//package com.hogent.mindfulness.data
//
//import android.arch.persistence.room.Database
//import android.arch.persistence.room.Room
//import android.arch.persistence.room.RoomDatabase
//import android.arch.persistence.room.TypeConverters
//import android.content.Context
//import com.hogent.mindfulness.domain.Model
//
//@Database(entities = arrayOf(Model.User::class), version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase(){
//    abstract fun userDao(): UserDao
//
//    companion object {
//
//        // For Singleton instantiation
//        @Volatile private var instance: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return instance ?: synchronized(this) {
//                instance ?: buildDatabase(context).also { instance = it }
//            }
//        }
//
//        private fun buildDatabase(context: Context): AppDatabase {
//            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "mindfulness_db")
//                .build()
//        }
//    }
//}