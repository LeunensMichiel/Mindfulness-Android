package com.hogent.mindfulness.domain

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


object Model {

    data class Sessionmap(
        val _id: String,
        val titleCourse: String,
        val sessions: Array<Session>
    )


    data class Session(
        val _id: String,
        val position: Int,
        val title: String
    )


    data class Exercise(
        val _id: String,
        val title: String
    )

    data class Page(
        val pathaudio: String,
        val type: String,
        val _id: String,
        val title: String,
        val description: String,
        val exercise_id: String,
        val paragraphs: Array<Paragraph>
    )

    data class Paragraph(
        val _id: String,
        val type: String,
        val filename: String,
        val pathname: String,
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
        val group: Group,
//        @ColumnInfo(name = "token")
        var token: String?,
        var post_ids: Array<String>
    )


    data class Group(
        val _id: String,
        val name: String,
        val sessionmap_id: String,
        var sessionmap: Sessionmap?
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

    data class unlock_session (
        val id: String,
        val session_id: String
    )

    data class Post(
        val _id:String,
        val inhoud:String,
        val afbeelding:String,
        val sessionmap_id:String,
        val session_id:String,
        val exercise_id:String,
        val page_id:String,
        val user_id:String
    )
}