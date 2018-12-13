package com.hogent.mindfulness.injection.module

import android.content.Context
import android.util.Log
import com.hogent.mindfulness.data.*
import com.hogent.mindfulness.data.API.UserApiService
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    val API_BASE_URL = "http://projecten3studserver03.westeurope.cloudapp.azure.com:3000"
    val LOCAL_BASE_URL = "http://172.18.155.92:3000"

    @Provides
    internal fun provideUserApi(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    internal fun provideSessionApi(retrofit: Retrofit):SessionApiService {
        return retrofit.create(SessionApiService::class.java)
    }

    @Provides
    internal fun provideFeedbackApi(retrofit: Retrofit):FeedbackApiService {
        return retrofit.create(FeedbackApiService::class.java)
    }

    @Provides
    internal fun provideExerciseApi(retrofit: Retrofit):ExerciseApiService {
        return retrofit.create(ExerciseApiService::class.java)
    }

    @Provides
    internal fun providePageApi(retrofit: Retrofit):PageApiService {
        return retrofit.create(PageApiService::class.java)
    }

    @Provides
    internal fun providePostApi(retrofit: Retrofit):PostApiService {
        return retrofit.create(PostApiService::class.java)
    }

    @Provides
    internal fun provideFileApi(retrofit: Retrofit):FIleApiService {
        return retrofit.create(FIleApiService::class.java)
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
    internal fun provideOkHttpClient(authToken: String): OkHttpClient {
        if (authToken != "none") {
            Log.i("AUTH_TOKEN", authToken)
            val interceptor = AuthenticationInterceptor(authToken)
            return OkHttpClient.Builder().apply {
                addInterceptor(interceptor)
            }.build()
        }

        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()
    }

    @Provides
    internal fun getAuthToken(context: Context): String {
        return context
            .getSharedPreferences("userDetails", Context.MODE_PRIVATE)
            .getString("authToken", "none")!!
    }
}
