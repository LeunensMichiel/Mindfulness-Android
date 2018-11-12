package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface PageApiService {
    /**
     * REST API Call for retrieving all pages from the given exercisesid
     */
    @GET("/API/pages/{id}")
    fun getPages(@Path("id") id: String): Observable<Array<Model.Page>>
}