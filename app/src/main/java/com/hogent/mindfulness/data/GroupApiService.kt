package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupApiService {
    /**
     * REST API Call for retrieving a specified group
     */
    @GET("/API/group/group/{id}")
    fun getGroup(@Path("id") id: String): Observable<Model.Group>
}