package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.network.RetrofitInstance.api
import com.example.androidakademijaprojekt.network.model.TaskRequest
import com.example.androidakademijaprojekt.network.model.TaskResponse

class TaskRepository {

    suspend fun getAllTasks(authToken: String): List<TaskResponse> {
        val response = api.getAllTasks(
            authToken = authHeader(authToken)
        )

        return response.tasks
    }

    suspend fun getTaskById(
        authToken: String,
        taskId: String
    ): TaskResponse {
        return api.getTaskById(
            authToken = authHeader(authToken),
            taskId = taskId
        )
    }

    suspend fun createTask(
        authToken: String,
        title: String,
        body: String
    ): TaskResponse {
        return api.createTask(
            authToken = "Bearer $authToken",
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
        api.updateTask(
            authToken = "Bearer $authToken",
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
        api.deleteTask(
            authToken = authHeader(authToken),
            taskId = taskId
        )
    }

    private fun authHeader(authToken: String): String {
        return "Bearer $authToken"
    }
}