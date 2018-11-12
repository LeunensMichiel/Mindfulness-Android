package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ExerciseApiService {
    /**
     * REST API Call for retrieving all exercises from the given sessionid
     */
    @GET("/API/exercises/{id}")
    fun getExercises(@Path("id") id: String): Observable<Array<Model.Exercise>>


}