package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.network.RetrofitInstance
import com.example.androidakademijaprojekt.network.model.TaskResponse

class TaskRepository {

    suspend fun getAllTasks(authToken: String): List<TaskResponse> {
        val response = RetrofitInstance.api.getAllTasks(
            authToken = "Bearer $authToken"
        )

        return response.tasks
    }
}