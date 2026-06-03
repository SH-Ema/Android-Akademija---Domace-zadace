package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.logger.AppLogger
import com.example.androidakademijaprojekt.network.TaskieApiService
import com.example.androidakademijaprojekt.network.model.LoginRequest
import com.example.androidakademijaprojekt.network.model.LoginResponse
import com.example.androidakademijaprojekt.network.model.TaskListResponse
import com.example.androidakademijaprojekt.network.model.TaskRequest
import com.example.androidakademijaprojekt.network.model.TaskResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    private val logger = AppLogger("AuthRepositoryTest")

    @Test
    fun login_returnsTrueAndStoresToken_whenApiReturnsToken() = runBlocking {
        val api = FakeTaskieApiService(loginToken = "test-token")
        val repository = AuthRepository(api, logger)

        val result = repository.login(
            username = "ema",
            password = "password123"
        )

        assertTrue(result)
        assertEquals("test-token", repository.authToken)
        assertEquals(LoginRequest("ema", "password123"), api.lastLoginRequest)
    }

    @Test
    fun login_returnsFalseAndDoesNotStoreToken_whenApiThrowsException() = runBlocking {
        val api = FakeTaskieApiService(shouldLoginFail = true)
        val repository = AuthRepository(api, logger)

        val result = repository.login(
            username = "wrong",
            password = "wrong"
        )

        assertFalse(result)
        assertNull(repository.authToken)
    }
}

private class FakeTaskieApiService(
    private val loginToken: String = "token",
    private val shouldLoginFail: Boolean = false
) : TaskieApiService {

    var lastLoginRequest: LoginRequest? = null

    override suspend fun login(request: LoginRequest): LoginResponse {
        lastLoginRequest = request

        if (shouldLoginFail) {
            throw RuntimeException("Login failed")
        }

        return LoginResponse(token = loginToken)
    }

    override suspend fun getAllTasks(authToken: String): TaskListResponse {
        return TaskListResponse(tasks = emptyList())
    }

    override suspend fun getTaskById(
        authToken: String,
        taskId: String
    ): TaskResponse {
        return TaskResponse(
            id = taskId,
            title = "Test title",
            body = "Test body"
        )
    }

    override suspend fun createTask(
        authToken: String,
        request: TaskRequest
    ): TaskResponse {
        return TaskResponse(
            id = "created-id",
            title = request.title,
            body = request.body
        )
    }

    override suspend fun updateTask(
        authToken: String,
        taskId: String,
        request: TaskRequest
    ) {}

    override suspend fun deleteTask(
        authToken: String,
        taskId: String
    ) {}
}