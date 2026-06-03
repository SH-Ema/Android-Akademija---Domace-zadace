package com.example.androidakademijaprojekt.viewmodel

import com.example.androidakademijaprojekt.logger.AppLogger
import com.example.androidakademijaprojekt.network.TaskieApiService
import com.example.androidakademijaprojekt.network.model.LoginRequest
import com.example.androidakademijaprojekt.network.model.LoginResponse
import com.example.androidakademijaprojekt.network.model.TaskListResponse
import com.example.androidakademijaprojekt.network.model.TaskRequest
import com.example.androidakademijaprojekt.network.model.TaskResponse
import com.example.androidakademijaprojekt.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class LoginViewModelTest {

    private val logger = AppLogger("LoginViewModelTest")

    @Test
    fun onUsernameChange_updatesUsernameAndClearsErrorMessage() {
        val viewModel = LoginViewModel(
            authRepository = AuthRepository(FakeTaskieApiService(), logger),
            logger = logger
        )

        viewModel.onUsernameChange("ema")

        val state = viewModel.uiState.value

        assertEquals("ema", state.username)
        assertNull(state.errorMessage)
    }

    @Test
    fun onPasswordChange_updatesPasswordAndClearsErrorMessage() {
        val viewModel = LoginViewModel(
            authRepository = AuthRepository(FakeTaskieApiService(), logger),
            logger = logger
        )

        viewModel.onPasswordChange("password123")

        val state = viewModel.uiState.value

        assertEquals("password123", state.password)
        assertNull(state.errorMessage)
    }

    @Test
    fun login_showsErrorMessage_whenUsernameOrPasswordIsBlank() {
        val viewModel = LoginViewModel(
            authRepository = AuthRepository(FakeTaskieApiService(), logger),
            logger = logger
        )

        viewModel.login()

        val state = viewModel.uiState.value

        assertEquals("Enter username and password.", state.errorMessage)
        assertFalse(state.isLoggedIn)
        assertFalse(state.isLoading)
    }
}

private class FakeTaskieApiService : TaskieApiService {

    override suspend fun login(request: LoginRequest): LoginResponse {
        return LoginResponse(token = "fake-token")
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