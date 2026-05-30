package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.logger.AppLogger
import com.example.androidakademijaprojekt.network.TaskieApiService
import com.example.androidakademijaprojekt.network.model.LoginRequest

class AuthRepository(
    private val api: TaskieApiService,
    private val logger: AppLogger
) {

    var authToken: String? = null
        private set

    suspend fun login(username: String, password: String): Boolean {
        logger.logI("Login started.")

        return try {
            val response = api.login(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

            authToken = response.token
            logger.logI("Login successful.")
            true
        } catch (exception: Exception) {
            logger.logE("Login failed: ${exception.message}")
            false
        }
    }
}