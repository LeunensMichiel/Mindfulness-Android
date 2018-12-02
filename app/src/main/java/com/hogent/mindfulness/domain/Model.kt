package com.hogent.mindfulness.domain

import com.google.gson.annotations.SerializedName
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
        val description: String
    )

    data class User(
        val _id: String,
        val firstname: String,
        val lastname: String,
        val email: String,
        val current_session_id: String,
        val current_exercise_id: String,
//        @ColumnInfo(name = "current_session")
        var current_session: Session?,
//        @ColumnInfo(name = "current_exercise")
        var current_exercise: Exercise?,
//        @ColumnInfo(name = "unlocked_sessions")
        var unlocked_sessions: Array<String>,
//        @ColumnInfo(name = "group")
        var group: Group?,
//        @ColumnInfo(name = "token")
        var token: String?,
        var post_ids: Array<String>,
        var feedbackSubscribed: Boolean
    )

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