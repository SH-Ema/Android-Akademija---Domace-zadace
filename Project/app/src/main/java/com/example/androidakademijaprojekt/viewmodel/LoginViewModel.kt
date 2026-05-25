package com.example.androidakademijaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidakademijaprojekt.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    val authToken: String?
        get() = authRepository.authToken

    fun onUsernameChange(newUsername: String) {
        _uiState.update { currentState ->
            currentState.copy(
                username = newUsername,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = newPassword,
                errorMessage = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter username and password.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val success = authRepository.login(
                username = currentState.username,
                password = currentState.password
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = success,
                    errorMessage = if (success) {
                        null
                    } else {
                        "Login failed. Check your username and password."
                    }
                )
            }
        }
    }
}