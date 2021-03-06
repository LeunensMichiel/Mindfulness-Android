package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import okhttp3.MultipartBody
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

    @Multipart
    @POST("/API/post/post/image")
    fun addImagePost(@Part("post") post: Model.Post, @Part file:MultipartBody.Part):Observable<Model.Post>

    @Multipart
    @PUT("/API/post/post/image/{post_image_id}")
    fun changeImagePost(@Path("post_image_id") id: String, @Part file:MultipartBody.Part):Observable<Model.toastMessage>
}