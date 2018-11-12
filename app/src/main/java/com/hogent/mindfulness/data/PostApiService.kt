package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostApiService {

    @POST("/API/post/getpost")
    fun getPostOfPage(@Body postInformation:PostInformation): Observable<Model.Post>

    @PUT("/API/post/post/{id}")
    fun updatePost(@Path("id") id:String, @Body postInformation: PostInformation): Observable<Model.Post>

    @POST("/API/post/post")
    fun AddPost(@Body postInformation:PostInformation): Observable<Model.Post>
}