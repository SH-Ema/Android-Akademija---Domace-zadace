package com.example.androidakademijaprojekt.network

import com.example.androidakademijaprojekt.network.model.LoginRequest
import com.example.androidakademijaprojekt.network.model.LoginResponse
import com.example.androidakademijaprojekt.network.model.TaskListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TaskieApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("tasks/all")
    suspend fun getAllTasks(
        @Header("Authorization") authToken: String
    ): TaskListResponse
}