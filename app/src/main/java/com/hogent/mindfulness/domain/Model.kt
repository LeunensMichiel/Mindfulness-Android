package com.hogent.mindfulness.domain

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import io.reactivex.annotations.NonNull
import java.util.*


object Model {

    data class Sessionmap(
        val _id: String,
        val titleCourse: String,
        val sessions: Array<Session>
    )


    data class Session(
        val _id: String,
        val position: Int,
        val title: String,
        @SerializedName("image_filename_session")
        val imageFilename: String
    )


    data class Exercise(
        val _id: String,
        val title: String
    )

    data class Page(
        @SerializedName("audio_filename")
        val audioFilename: String,
        val type: String,
        val _id: String,
        val title: String,
        val description: String,
        val exercise_id: String,
        val paragraphs: Array<Paragraph>
    )

    data class Paragraph(
        val _id: String,
        @SerializedName("form_type")
        val type: String,
        @SerializedName("image_filename")
        val imageFilename: String,
        val description: String,
        val position: Int,
        val pathname:String,
        var bitmap: Bitmap? = null
    )

    @Entity(tableName = "user_table")
    data class User(
        var _id: String? = null,
        var firstname: String? = null,
        var lastname: String? = null,
        var email: String? = null,
        var current_session_id: String? = null,
        var current_exercise_id: String? = null,
//        @ColumnInfo(name = "current_session")
        @Ignore
        var current_session: Session?,
//        @ColumnInfo(name = "current_exercise")
        @Ignore
        var current_exercise: Exercise?,
//        @ColumnInfo(name = "unlocked_sessions")
        @Ignore
        var unlocked_sessions: Array<String>,
//        @ColumnInfo(name = "group")
        @Ignore
        var group: Group?,
//        @ColumnInfo(name = "token")
        var token: String?,
        @Ignore
        var post_ids: ArrayList<String>,
        var feedbackSubscribed: Boolean = false
    ) {
        constructor():this(
            null,
            null,
            null,
            null,
            null,
            null ,
            null,
            null,
            arrayOf(),
            null,
            null, arrayListOf(), false)
        @PrimaryKey(autoGenerate = true)
        var db_id: Int = 0
    }

    data class Group(
        var _id: String,
        var name: String,
        var sessionmap_id: String,
        var sessionmap: Sessionmap? = null
    )

    data class Login(
        val email: String,
        val password: String
    )

    data class Register(
        val email: String,
        val password: String,
        val groups_code: String
    )

    data class Result(
        val result: String
    )

    data class unlock_session(
        val id: String,
        val session_id: String
    )

    data class uiMessage(
        var data:String
    )

    data class errorMessage(
        var data:String,
        var error:String
    )

    data class user_group (
        val group_id: String
    )

    data class Post(
        var _id:String? = "none",
        var inhoud:String? = null,
        var afbeelding:String? = null,
        var sessionmap_id:String? = null,
        var session_id:String? = null,
        var exercise_id:String? = null,
        var page_id:String? = null,
        var user_id:String? = null,
        var session_map_name:String? = null,
        var session_name:String? = null,
        var exercise_name:String? = null,
        var page_name:String? = null
    )

    data class Point(
        val x: Int,
        val y: Int,
        val orientation: Boolean
    )

    data class Feedback(
        val date: Date,
        val message: String,
        val session: String
    )

    data class File(
        val path:String?
    )
}