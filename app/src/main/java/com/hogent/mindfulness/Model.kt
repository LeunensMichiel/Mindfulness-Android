package com.hogent.mindfulness

object Model {
    data class Sessionmap(val _id: String,
                          val titleCourse: String,
                          val sessions: Array<Session>)

    data class Session(val _id: String,
                      val title: String,
                      val exercises: Array<Exercise>)

    data class Exercise(val _id: String,
                        val title: String)
}