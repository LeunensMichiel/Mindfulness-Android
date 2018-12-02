package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.*

interface PostApiService {

    @POST("/API/post/getpost")
    fun getPostOfPage(@Body postInformation:PostInformation): Observable<Model.Post>

    @PUT("/API/post/post/{id}")
    fun updatePost(@Path("id") id:String, @Body postInformation: PostInformation): Observable<Model.Post>

    @POST("/API/post/post")
    fun addPost(@Body post:Model.Post): Observable<Model.Post>

    @PUT("/API/post/post")
    fun changePost(@Body post:Model.Post):Observable<Model.Post>

    @GET("/API/post/checkpost/{page_id}")
    fun checkPageId(@Path("page_id") page_id:String):Observable<Model.Post>

    @GET("/API/post/post/{user_id}")
    fun getPosts(@Path("user_id") user_id:String):Observable<Array<Model.Post>>
}