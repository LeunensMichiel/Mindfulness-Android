package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface FIleApiService {

    @GET("/API/file/file")
    fun getFile(@Query("object_type") object_type: String, @Query("file_name") file_name: String): Observable<ResponseBody>
}