package com.hogent.mindfulness.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthenticationInterceptor(val authToken: String): Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        val builder = oldRequest.newBuilder()
            .header("Authorization", authToken)

        val newRequest = builder.build()

        return chain.proceed(newRequest)
    }

}