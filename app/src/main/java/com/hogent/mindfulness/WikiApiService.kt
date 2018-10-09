package com.hogent.mindfulness

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface WikiApiService {

    @GET("/API/mindfulness")
    fun hitCountCheck(): Observable<Model.Result>

    companion object {
        fun create(): WikiApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                            GsonConverterFactory.create())
                    .baseUrl("http://172.18.179.202:3000")
                    .build()

            return retrofit.create(WikiApiService::class.java)

        }
    }
}