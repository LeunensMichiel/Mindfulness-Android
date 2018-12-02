package com.hogent.mindfulness.data

import com.hogent.mindfulness.domain.Model
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApiService {
    @POST("/API/feedback/feedback")
    fun addFeedback(@Body response: Model.Feedback): Observable<Model.Result>
}