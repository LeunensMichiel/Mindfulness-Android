package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface SessionApiService {
    /**
     * REST API Call for retrieving one sessionmap from the given id
     */
    @GET("/API/session/sessions/{id}")
    fun getSessions(@Path("id") id: String): Observable<Array<Model.Session>>

    @GET("API/session/session/{sesId}")
    fun getSessionById(@Path("sesId") id: String): Observable<Model.Session>
}