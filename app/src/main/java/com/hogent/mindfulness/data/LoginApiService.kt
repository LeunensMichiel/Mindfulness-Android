package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("/users/login")
    fun login(@Body response: Model.Login): Observable<Model.User>

    @POST("users/register")
    fun register(@Body response: Model.Register): Observable<Model.User>
}