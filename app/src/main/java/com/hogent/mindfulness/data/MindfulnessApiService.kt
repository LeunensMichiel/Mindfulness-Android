package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Path

interface MindfulnessApiService {

    // REST API Call for retrieving one sessionmap from the given id
    @GET("/API/sessions/{id}")
    fun getSessionmap(@Path("id") id: String): Observable<Array<Model.Session>>

    // REST API Call for retrieving all exercises from the given sessionid
    @GET("/API/exercises/{id}")
    fun getExercises(@Path("id") id: String): Observable<Array<Model.Exercise>>

    // REST API Call for retrieving all pages from the given exercisesid
    @GET("/API/pages/{id}")
    fun getPages(@Path("id") id: String): Observable<Array<Model.Page>>


    // REST API Call for retrieving all paragraphs from the given pageid
    @GET("/API/paragraphs/{id}")
    fun getParagraphs(@Path("id") id: String): Observable<Array<Model.Paragraph>>

    /*
    @GET("/API/posts/{id}")
    fun getPost(@Path("id") id:String):Observable<Model.Post> */
    /*
    @POST("/API/getpost")
    fun getPost(@Body sessionmap_id:String, @Body session_id:String, @Body exercise_id:String, @Body page_id:String, @Body user_id:String)
            :Observable<Model.Post>
            */
    @POST("/API/getpost")
    fun getPost(@Body postInformation:PostInformation)
            :Observable<Model.Post>

//    @POST("/API/mindfulness")
//    fun sendResponse(@Body response: com.hogent.mindfulness.domain.Model.Response) : Observable<com.hogent.mindfulness.domain.Model.Result>

    companion object {
        fun create(): MindfulnessApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl("http://projecten3studserver03.westeurope.cloudapp.azure.com:3000")
                .build()

            return retrofit.create(MindfulnessApiService::class.java)

        }
    }
}