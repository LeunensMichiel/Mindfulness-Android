package com.hogent.mindfulness.data.LocalDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessContract.*
import com.hogent.mindfulness.domain.Model

class MindfulnessDBHelper ( context: Context ):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("db", "db creation")
        val SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry.COLUMN_ID + " TEXT PRIMARY KEY NOT NULL , " +
                UserEntry.COLUMN_CURRENT_SESSION_ID + " TEXT , " +
                UserEntry.COLUMN_CURRENT_EXERCISE_ID + " TEXT, " +
                UserEntry.COLUMN_UNLOCKED_SESSIONS + " TEXT, " +
                UserEntry.COLUMN_POSTS + " TEXT " +
                "); "
        Log.i("dbCOunt", "1")
        val SQL_CREATE_GROUP_TABLE = "CREATE TABLE " + GroupEntry.TABLE_NAME + " (" +
                GroupEntry.COLUMN_ID + " TEXT PRIMARY KEY NOT NULL , " +
                GroupEntry.COLUMN_NAME + " TEXT , " +
                GroupEntry.COLUMN_SESSIONMAP_ID + " TEXT " +
                "); "
        Log.i("dbCOunt", "2")
        db!!.execSQL(SQL_CREATE_USER_TABLE)
        Log.i("dbCOunt", "3")
        db!!.execSQL(SQL_CREATE_GROUP_TABLE)
        Log.i("dbCOunt", "4")
        Log.i("db", "db creation end")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME)
        db!!.execSQL("DROP TABLE IF EXISTS " + GroupEntry.TABLE_NAME)
        onCreate(db)
    }

    fun addUser(user: Model.User):Boolean {
        val userExists = doesDataExist(UserEntry.TABLE_NAME, UserEntry.COLUMN_ID , user._id)
        if (!userExists){
            val values = ContentValues()
            val unlocked_sessions = user.unlocked_sessions.joinToString(",", "", "")
            val post_ids = user.post_ids.joinToString(",", "", "")

            values.put(UserEntry.COLUMN_ID, user._id)
            values.put(UserEntry.COLUMN_CURRENT_SESSION_ID, user.current_session_id)
            values.put(UserEntry.COLUMN_CURRENT_EXERCISE_ID, user.current_exercise_id)
            values.put(UserEntry.COLUMN_UNLOCKED_SESSIONS, unlocked_sessions)
            values.put(UserEntry.COLUMN_POSTS, post_ids)

            val db = this.writableDatabase
            Log.i("dbUser", values.toString())
            db.insert(UserEntry.TABLE_NAME,null,  values)
            db.close()

            addGroup(user.group)
        }
        return userExists
    }

    fun addGroup(group: Model.Group){
        val values = ContentValues()

        values.put(GroupEntry.COLUMN_ID, group._id)
        values.put(GroupEntry.COLUMN_NAME, group.name)
        values.put(GroupEntry.COLUMN_SESSIONMAP_ID, group.sessionmap_id)

        Log.i("dbGroup", values.toString())
        val db = this.writableDatabase
        db.insert(GroupEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun doesDataExist(TableName: String, dbfield: String, fieldValue: String): Boolean {
        val sqldb =this.readableDatabase
        val Query = "Select * from $TableName where $dbfield =\"$fieldValue\""
        val cursor = sqldb.rawQuery(Query, null)
        if (cursor.getCount() <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    companion object {
        private val DATABASE_NAME = "mindfulness.db"
        private val DATABASE_VERSION = 1
    }
}