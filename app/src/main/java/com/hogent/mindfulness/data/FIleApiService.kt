package com.hogent.mindfulness.data

import okhttp3.ResponseBody
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface FIleApiService {

    @GET("/API/file/file")
    fun getFile(@Query("object_type") object_type: String, @Query("file_name") file_name: String): Observable<ResponseBody>
}