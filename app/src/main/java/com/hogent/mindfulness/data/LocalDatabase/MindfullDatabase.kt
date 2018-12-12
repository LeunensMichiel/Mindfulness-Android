package com.hogent.mindfulness.data.LocalDatabase

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.hogent.mindfulness.data.LocalDatabase.converter.GroupConverter
import com.hogent.mindfulness.data.LocalDatabase.converter.StringArrayListConverter
import com.hogent.mindfulness.data.LocalDatabase.dao.UserDao
import com.hogent.mindfulness.domain.Model

@Database(entities = [Model.User::class], version = 3)
@TypeConverters(StringArrayListConverter::class, GroupConverter::class)
abstract class MindfullDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MindfullDatabase? = null
        const val DATABASE_NAME = "mindfullnessdb"

        fun getDatabase(context: Context): MindfullDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindfullDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}