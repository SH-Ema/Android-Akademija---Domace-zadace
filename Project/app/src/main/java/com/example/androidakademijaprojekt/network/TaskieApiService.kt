package com.example.androidakademijaprojekt.network

import com.example.androidakademijaprojekt.network.model.LoginRequest
import com.example.androidakademijaprojekt.network.model.LoginResponse
import com.example.androidakademijaprojekt.network.model.TaskListResponse
import com.example.androidakademijaprojekt.network.model.TaskRequest
import com.example.androidakademijaprojekt.network.model.TaskResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskieApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("tasks/all")
    suspend fun getAllTasks(
        @Header("Authorization") authToken: String
    ): TaskListResponse

    @GET("tasks/{id}")
    suspend fun getTaskById(
        @Header("Authorization") authToken: String,
        @Path("id") taskId: String
    ): TaskResponse

    @POST("tasks/create")
    suspend fun createTask(
        @Header("Authorization") authToken: String,
        @Body request: TaskRequest
    ): TaskResponse

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Header("Authorization") authToken: String,
        @Path("id") taskId: String,
        @Body request: TaskRequest
    )

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Header("Authorization") authToken: String,
        @Path("id") taskId: String
    )
}