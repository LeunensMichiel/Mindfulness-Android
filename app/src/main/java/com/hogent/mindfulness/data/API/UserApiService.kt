package com.hogent.mindfulness.data.API

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.*

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
    fun getUser( @Path("id") id:String?): Observable<Model.User>

    /**
     * unlocks a session with a code (code == session_id)
     */
    @POST("/API/users/user")
    fun updateUser(@Body unlocksession: Model.unlock_session) : Observable<Model.Result>

    /**
     * registers a user in a group with a code (code == group_id)
     */
    @PUT("/users/user{id}")
    fun updateUserGroup(@Path("id") id: String, @Body usergroup: Model.user_group) : Observable<Model.Result>

    @GET("/API/users/group/{id}")
    fun getUserGroup(@Path("id") id:String?):Observable<Model.Group>

    //Updates a USER so its feedback field is true or false
    @PUT("/API/users/user/feedback")
    fun updateUserFeedback(@Body wantsFeedback : Model.User) : Observable<Model.Result>
}