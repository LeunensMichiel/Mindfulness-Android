package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import io.reactivex.Observable
import java.util.*

interface FIleApiService {

    @POST("/API/file/file")
    fun getFile(@Body file: Model.File):Observable<ResponseBody>
}