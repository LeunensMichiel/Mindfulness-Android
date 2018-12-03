package com.hogent.mindfulness.data

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {

    companion object {

        val API_BASE_URL = "http://projecten3studserver03.westeurope.cloudapp.azure.com:3000"
        val LOCAL_BASE_URL = "http://10.1.48.141:3000"
        val httpClient = OkHttpClient.Builder()

        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())

        var retrofit = builder.build()

        fun <S> createService(serviceClass: Class<S>, activity: Activity): S {
            val token = activity!!.getSharedPreferences("userDetails", Context.MODE_PRIVATE)
                .getString("authToken", null)
            return createService(serviceClass, authToken = token)
        }

        fun <S> createService(
            serviceClass: Class<S>,
            authToken: String?
        ): S {
            if (!TextUtils.isEmpty(authToken)) {
                val interceptor = AuthenticationInterceptor(authToken!!)

                if (!httpClient.interceptors().contains(interceptor)) {
                    httpClient.addInterceptor(interceptor)

                    builder.client(httpClient.build())

                    retrofit = builder.build()
                }
            }

            return retrofit.create(serviceClass)
        }
    }
}