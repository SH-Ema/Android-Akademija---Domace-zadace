package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.network.RetrofitInstance
import com.example.androidakademijaprojekt.network.model.TaskRequest
import com.example.androidakademijaprojekt.network.model.TaskResponse

class TaskRepository {

    suspend fun getAllTasks(authToken: String): List<TaskResponse> {
        val response = RetrofitInstance.api.getAllTasks(
            authToken = authHeader(authToken)
        )

        return response.tasks
    }

    suspend fun getTaskById(
        authToken: String,
        taskId: String
    ): TaskResponse {
        return RetrofitInstance.api.getTaskById(
            authToken = authHeader(authToken),
            taskId = taskId
        )
    }

    suspend fun createTask(
        authToken: String,
        title: String,
        body: String
    ): TaskResponse {
        return RetrofitInstance.api.createTask(
            authToken = authHeader(authToken),
            request = TaskRequest(
                title = title,
                body = body
            )
        )
    }

    suspend fun updateTask(
        authToken: String,
        taskId: String,
        title: String,
        body: String
    ) {
        RetrofitInstance.api.updateTask(
            authToken = authHeader(authToken),
            taskId = taskId,
            request = TaskRequest(
                title = title,
                body = body
            )
        )
    }

    suspend fun deleteTask(
        authToken: String,
        taskId: String
    ) {
        RetrofitInstance.api.deleteTask(
            authToken = authHeader(authToken),
            taskId = taskId
        )
    }

    private fun authHeader(authToken: String): String {
        return "Bearer $authToken"
    }
}