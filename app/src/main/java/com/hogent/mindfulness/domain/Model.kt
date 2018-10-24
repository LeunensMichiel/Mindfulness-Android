package com.hogent.mindfulness.domain

object Model {
    data class Sessionmap(val _id: String,
                          val titleCourse: String,
                          val sessions: Array<Session>)

    data class Session(val _id: String,
                      val title: String,
                      val exercises: Array<Exercise>)

    data class Exercise(val id: String,
                        val title: String)

    data class Page(val pathaudio:String,
                     val type:String,
                    val _id:String,
                    val title:String,
                    val description:String,
                    val exercise_id:String)
}