package com.hogent.mindfulness.domain

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import android.media.MediaPlayer
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
        @SerializedName("image_filename")
        val imageFilename: String,
        var bitmap: Bitmap? = null,
        var unlocked:Boolean = false
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
        val paragraphs: Array<Paragraph>,
        var audioFile:File? = null,
        var mediaPlayer: MediaPlayer? = null,
        var progress:Int? = 0
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
        var unlocked_sessions: ArrayList<String> = arrayListOf(),
//        @ColumnInfo(name = "group")
        var group: Group?,
//        @ColumnInfo(name = "token")
        var token: String?,
        var post_ids: ArrayList<String> = arrayListOf(),
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
            arrayListOf(),
            null,
            null, arrayListOf(), false)
        @PrimaryKey(autoGenerate = true)
        var db_id: Int = 0
    }

    @Entity(tableName = "group_table")
    data class Group(
        var _id: String? = null,
        var name: String? = null,
        var sessionmap_id: String? = null,
        var sessionmap: Sessionmap? = null
    ) {
        constructor():this(
            null,
            null,
            null,
            null
        )
        @PrimaryKey
        var db_id: Int = 0
    }

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
        var data:String? = "none"
    )

    data class errorMessage(
        var data:String = "none",
        var error:String = "none"
    )

    data class loginErrorMessage(
        var data:String? = null,
        var email:String? = null,
        var password:String? = null
    )

    data class user_group (
        val group_id: String
    )

    data class Post(
        var _id:String? = "none",
        var inhoud:String? = null,
        var afbeelding:String? = null,
        var image_file_name: String? = null,
        var sessionmap_id:String? = null,
        var session_id:String? = null,
        var exercise_id:String? = null,
        var page_id:String? = null,
        var user_id:String? = null,
        var session_map_name:String? = null,
        var session_name:String? = null,
        var exercise_name:String? = null,
        var page_name:String? = null,
        var bitmap: Bitmap? = null
    )

    data class Point(
        val x: Int,
        val y: Int,
        val orientation: Boolean
    )

    data class Feedback(
        var date: Date,
        var message: String,
        var session: String? = null
    )

    data class File(
        val path:String?
    )

    data class toastMessage(
        var message:String? = null
    )
}