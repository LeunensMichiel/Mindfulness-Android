package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import io.reactivex.Observable
import retrofit2.http.Path

interface MindfulnessApiService {

    @GET("/API/sessionmap/{id}")
    fun getSessionmap(@Path("id") id: String): Observable<Model.Sessionmap>

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