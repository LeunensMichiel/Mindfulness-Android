package com.hogent.mindfulness.injection.module

import android.app.Application
import android.content.Context
import com.hogent.mindfulness.data.AuthenticationInterceptor
import com.hogent.mindfulness.data.UserApiService
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    val API_BASE_URL = "http://projecten3studserver03.westeurope.cloudapp.azure.com:3000"
    val LOCAL_BASE_URL = "http://172.18.155.92:3000"

    @Provides
    internal fun provideUserApi(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    internal fun provideRetrofitInterface(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    internal fun provideOkHttpClient(authToken: String?): OkHttpClient {

        val interceptor = AuthenticationInterceptor(authToken!!)

        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()
    }

    @Provides
    @Singleton
    internal fun getAuthToken(context:Context):String? {
        return context
            .getSharedPreferences("userDetails", Context.MODE_PRIVATE)
            .getString("authToken", null)
    }
}
