package com.CargoTrack.cargotrack.Client

import com.CargoTrack.cargotrack.API.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    const val BASE_URL = "https://iso-ocr.azurewebsites.net/"

    /*val client = OkHttpClient()
        .newBuilder()
        .build()*/
    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory((RxJava2CallAdapterFactory.create()))
        .addConverterFactory((GsonConverterFactory.create()))
        .client(client)
        .build()
        .create(ApiService::class.java)

    fun buildService(): ApiService {
        return retrofit
    }

}