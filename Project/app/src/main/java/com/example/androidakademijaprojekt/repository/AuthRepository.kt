package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.network.RetrofitInstance
import com.example.androidakademijaprojekt.network.model.LoginRequest

class AuthRepository {

    var authToken: String? = null
        private set

    suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = RetrofitInstance.api.login(
                LoginRequest(
                    username = username,
                    password = password
                )
            )

            authToken = response.token
            true
        } catch (exception: Exception) {
            false
        }
    }
}