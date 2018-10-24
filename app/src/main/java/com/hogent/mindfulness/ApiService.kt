
package com.hogent.mindfulness

/*import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @GET("/API/sessionmap/5bcf59077fc71d412c348b55")
    fun getResult(): Observable<Model.Result>

    @POST("/API/mindfulness")
    fun sendResponse(@Body response: Model.Response) : Observable<Model.Result>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl("projecten3studserver03.westeurope.cloudapp.azure.com:3000") // TODO Change IP to local IP
                .build()

            return retrofit.create(ApiService::class.java)

        }
    }
}*/