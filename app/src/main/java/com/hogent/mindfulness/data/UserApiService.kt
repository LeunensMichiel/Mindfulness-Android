package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @POST("/API/users/login")
    fun login(@Body response: Model.Login): Observable<Model.User>

    @POST("/API/users/register")
    fun register(@Body response: Model.Register): Observable<Model.User>

    /**
     * Gets the details from a user
     * This API call should be eliminated in the future with a locale database (sqllite)
     */
    @GET("/API/users/user/{id}")
    fun getUser(@Path("id") id:String?): Observable<Model.User>

    /**
     * unlocks a session with a code (code == session_id)
     */
    @POST("/API/users/user")
    fun updateUser(@Body unlocksession: Model.unlock_session) : Observable<Model.Result>

}