package com.hogent.mindfulness.domain


object Model {

    data class Sessionmap(
        val _id: String,
        val titleCourse: String,
        val sessions: Array<Session>
    )


    data class Session(
        val _id: String,
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
        var current_session: Session?,
        var current_exercise: Exercise?,
        val group: Group,
        var token: String?
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
}