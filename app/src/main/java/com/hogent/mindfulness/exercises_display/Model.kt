package com.hogent.mindfulness.exercises_display

object Model {
    data class Session(val id_session: Int,
                      val title: String,
                      val position: Int,
                      val exercises: List<Exercise>)

    data class Exercise(val id_ex: Int,
                        val hexaflex_badge: Int,
                        val title: String,
                        val position: Int)
}