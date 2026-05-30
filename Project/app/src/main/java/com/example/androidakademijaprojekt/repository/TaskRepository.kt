package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.database.TaskDao
import com.example.androidakademijaprojekt.database.TaskEntity
import com.example.androidakademijaprojekt.database.toEntity
import com.example.androidakademijaprojekt.database.toEntityList
import com.example.androidakademijaprojekt.database.toResponse
import com.example.androidakademijaprojekt.database.toResponseList
import com.example.androidakademijaprojekt.logger.AppLogger
import com.example.androidakademijaprojekt.network.TaskieApiService
import com.example.androidakademijaprojekt.network.model.TaskRequest
import com.example.androidakademijaprojekt.network.model.TaskResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TaskRepository(
    private val api: TaskieApiService,
    private val taskDao: TaskDao,
    private val logger: AppLogger
) {

    fun observeTasks(): Flow<List<TaskResponse>> {
         return taskDao.observeTasks().map { taskEntities ->
            taskEntities.toResponseList()
        }
    }

    suspend fun getAllTasks(authToken: String): List<TaskResponse> {
        logger.logI("Loading tasks.")

        return try {
            val response = api.getAllTasks(
                authToken = authHeader(authToken)
            )

            taskDao.insertTasks(response.tasks.toEntityList())
            logger.logI("Remote tasks saved to local database.")

            taskDao.getAllTasksOnce().toResponseList()
        } catch (exception: Exception) {
            taskDao.getAllTasksOnce().toResponseList()
        }
    }

    suspend fun getTaskById(
        authToken: String,
        taskId: String
    ): TaskResponse {
        val localTask = taskDao.getTaskById(taskId)

        if (localTask != null) {
            return localTask.toResponse()
        }

        val remoteTask = api.getTaskById(
            authToken = authHeader(authToken),
            taskId = taskId
        )

        taskDao.insertTask(remoteTask.toEntity())

        return remoteTask
    }

    suspend fun createTask(
        authToken: String,
        title: String,
        body: String
    ): TaskResponse {
        val localTask = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            body = body,
            createdAt = todayDate(),
            isSynced = false
        )

        taskDao.insertTask(localTask)
        logger.logI("Task saved locally.")

        return try {
            val remoteTask = api.createTask(
                authToken = authHeader(authToken),
                request = TaskRequest(
                    title = title,
                    body = body
                )
            )

            taskDao.deleteTaskById(localTask.id)
            taskDao.insertTask(remoteTask.toEntity(isSynced = true))

            logger.logI("Task synced with server.")
            remoteTask
        } catch (exception: Exception) {
            localTask.toResponse()
        }
    }

    suspend fun updateTask(
        authToken: String,
        taskId: String,
        title: String,
        body: String
    ) {
        val existingTask = taskDao.getTaskById(taskId)

        val localTask = TaskEntity(
            id = taskId,
            title = title,
            body = body,
            createdAt = existingTask?.createdAt ?: todayDate(),
            isSynced = false
        )

        taskDao.insertTask(localTask)
        logger.logI("Task updated locally.")

        try {
            api.updateTask(
                authToken = authHeader(authToken),
                taskId = taskId,
                request = TaskRequest(
                    title = title,
                    body = body
                )
            )

            taskDao.insertTask(localTask.copy(isSynced = true))
        } catch (exception: Exception) {
        }
    }

    suspend fun deleteTask(
        authToken: String,
        taskId: String
    ) {
        taskDao.deleteTaskById(taskId)
        logger.logI("Task deleted locally.")

        try {
            api.deleteTask(
                authToken = authHeader(authToken),
                taskId = taskId
            )

            logger.logI("Task deleted from server.")
        } catch (exception: Exception) {
        }
    }

    suspend fun syncTasks(authToken: String) {
        logger.logI("Syncing tasks with server.")

        val response = api.getAllTasks(
            authToken = authHeader(authToken)
        )

        taskDao.insertTasks(response.tasks.toEntityList())
        logger.logI("Tasks synced and saved to local database.")
    }

    private fun authHeader(authToken: String): String {
        return "Bearer $authToken"
    }

    private fun todayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}