package com.hogent.mindfulness.data

import android.text.TextUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {

    companion object {

        val API_BASE_URL = "http://projecten3studserver03.westeurope.cloudapp.azure.com:3000"
        val LOCAL_BASE_URL = "http://192.168.0.128:3000"
        val httpClient = OkHttpClient.Builder()

        val builder = Retrofit.Builder()
            .baseUrl(LOCAL_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())

        var retrofit = builder.build()

        fun <S> createService(serviceClass: Class<S>
//                              email: String,
//                              password: String
        ): S {
//            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
//                val authToken = Credentials.basic(email, password)
//
//                return createService(serviceClass, authToken)
//            }

            return createService(serviceClass, null)
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