package com.hogent.mindfulness.data.LocalDatabase

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessContract.GroupEntry
import com.hogent.mindfulness.data.LocalDatabase.MindfulnessContract.UserEntry
import com.hogent.mindfulness.domain.Model

class MindfulnessDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i("db", "db creation")
        val SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry.COLUMN_ID + " TEXT PRIMARY KEY NOT NULL , " +
                UserEntry.COLUMN_CURRENT_SESSION_ID + " TEXT , " +
                UserEntry.COLUMN_CURRENT_EXERCISE_ID + " TEXT, " +
                UserEntry.COLUMN_UNLOCKED_SESSIONS + " TEXT, " +
                UserEntry.COLUMN_POSTS + " TEXT, " +
                UserEntry.COLUMN_FEEDBACK_IS_SUBSCRIBED + " INTEGER DEFAULT 0 , " +
                UserEntry.COLUMN_EMAIL + " TEXT , " +
                UserEntry.COLUMN_FIRSTNAME + " TEXT , " +
                UserEntry.COLUMN_LASTNAME + " TEXT , " +
                UserEntry.COLUMN_GROUP_ID + " TEXT" +
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

    fun addUser(user: Model.User): Boolean {
        val userExists = doesDataExist(UserEntry.TABLE_NAME, UserEntry.COLUMN_ID, user._id!!)
        if (!userExists) {
            val values = ContentValues()
            val unlocked_sessions = user.unlocked_sessions.joinToString(",", "", "")
            val post_ids = user.post_ids.joinToString(",", "", "")
            val feedbackBooleon: Int = if (user.feedbackSubscribed) 1 else 0
            val firstname = user.firstname
            val lastname = user.lastname
            val email = user.email
            val group = user.group!!

            values.put(UserEntry.COLUMN_ID, user._id)
            values.put(UserEntry.COLUMN_CURRENT_SESSION_ID, user.current_session_id)
            values.put(UserEntry.COLUMN_CURRENT_EXERCISE_ID, user.current_exercise_id)
            values.put(UserEntry.COLUMN_UNLOCKED_SESSIONS, unlocked_sessions)
            values.put(UserEntry.COLUMN_POSTS, post_ids)
            values.put(UserEntry.COLUMN_FEEDBACK_IS_SUBSCRIBED, feedbackBooleon)
            values.put(UserEntry.COLUMN_EMAIL, email)
            values.put(UserEntry.COLUMN_FIRSTNAME, firstname)
            values.put(UserEntry.COLUMN_LASTNAME, lastname)
            //DIT ZOU MISSCHIEN VOOR FOUTEN KUNNEN ZORGEN WOEPSIE DUS ALS JE EEN
            // BUG HEBT MET DE GROEPEN OF USER 30/11/2018
            values.put(UserEntry.COLUMN_GROUP_ID, group._id)

            val db = this.writableDatabase
            Log.i("dbUser", values.toString())
            Log.i("dbUserValues", "$values")
            db.insert(UserEntry.TABLE_NAME, null, values)
            db.close()

            addGroup(user.group!!)
        }
        return userExists
    }

    fun addGroup(group: Model.Group) {
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
        val sqldb = this.readableDatabase
        val Query = "Select * from $TableName where $dbfield =\"$fieldValue\""
        val cursor = sqldb.rawQuery(Query, null)
        if (cursor.getCount() <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    fun getGroup(): Model.Group? {
        val query = "SELECT * FROM " + GroupEntry.TABLE_NAME
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var group: Model.Group? = null
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            val id = cursor.getString(0)
            val name: String = cursor.getString(1)
            val sessionmap_id = cursor.getString(2)
            group = Model.Group(
                id,
                name,
                sessionmap_id,
                null
            )
        }
        db.close()
        return group
    }

    fun getUser(): Model.User? {
        val query = "SELECT * FROM " + UserEntry.TABLE_NAME
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var user: Model.User? = null
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
            Log.d("cursortje", DatabaseUtils.dumpCursorToString(cursor))
            val id = cursor.getString(0)
            val current_session_id = cursor.getString(1) ?: ""
            val current_ex_id = cursor.getString(2) ?: ""
            val unlocked_sessions: ArrayList<String> = arrayListOf()
            cursor.getString(3).split(",").toTypedArray().forEach {
                unlocked_sessions.add(it)
            }
            val post_ids = arrayListOf<String>()
            cursor.getString(4).split(",").toTypedArray().forEach {
                post_ids.add(it)
            }
            post_ids.forEach {
            }
            val feedback: Boolean = cursor.getInt(5) == 1
            val email: String = cursor.getString(6) ?: ""
            val firstname: String = cursor.getString(7) ?: ""
            val lastname: String = cursor.getString(8) ?: ""
            val group: String = cursor.getString(9) ?: ""
            user = Model.User(
                id,
                firstname,
                lastname,
                email,
                current_session_id,
                current_ex_id,
                null,
                null,
                unlocked_sessions,
                getGroup(),
                "",
                post_ids,
                feedback
            )
        }
        db.close()
        return user
    }

    companion object {
        private val DATABASE_NAME = "mindfulness.db"
        private val DATABASE_VERSION = 1
    }
}