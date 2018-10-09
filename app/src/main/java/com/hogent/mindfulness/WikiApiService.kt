package com.hogent.mindfulness

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface WikiApiService {

    @GET("/API/mindfulness")
    fun getResult(): Observable<Model.Result>

    @POST("/API/mindfulness")
    fun sendResponse(@Body response: Model.Response) : Observable<Model.Result>

    companion object {
        fun create(): WikiApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create())
                    .baseUrl("http://192.168.0.110:3000") // TODO Change IP to local IP
                    .build()

            return retrofit.create(WikiApiService::class.java)

        }
    }
}