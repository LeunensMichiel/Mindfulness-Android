package com.hogent.mindfulness.data.LocalDatabase

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

abstract class MindfullDatabase:RoomDatabase() {
    abstract fun userDao():UserDao

    companion object {
        @Volatile
        private var INSTANCE: MindfullDatabase? = null
        const val DATABASE_NAME="mindfullnessdb"

        fun getDatabase(context: Context):MindfullDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindfullDatabase::class.java,
                    DATABASE_NAME
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
//                        doAsync {
//                            populateDatabase(INSTANCE!!.wordDao())
//                        }
                    }

                }).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}