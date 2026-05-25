package com.example.androidakademijaprojekt.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://ada-taskie-backend.osc-fr1.scalingo.io/"

    val api: TaskieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskieApiService::class.java)
    }
}