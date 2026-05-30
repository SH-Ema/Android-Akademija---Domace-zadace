package com.example.androidakademijaprojekt.viewmodel

import com.example.androidakademijaprojekt.logger.AppLogger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidakademijaprojekt.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val taskRepository: TaskRepository,
    private val logger: AppLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeLocalTasks()
    }

    private fun observeLocalTasks() {
        viewModelScope.launch {
            taskRepository.observeTasks().collect { tasks ->
                _uiState.update {
                    it.copy(
                        tasks = tasks,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadTasks(authToken: String?) {
        val token = requireAuthToken(authToken) ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                taskRepository.getAllTasks(token)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load tasks."
                    )
                }
            }
        }
    }

    fun deleteTask(
        authToken: String?,
        taskId: String
    ) {
        val token = requireAuthToken(authToken) ?: return

        viewModelScope.launch {
            try {
                taskRepository.deleteTask(
                    authToken = token,
                    taskId = taskId
                )

                _uiState.update {
                    it.copy(errorMessage = null)
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete task.")
                }
            }
        }
    }

    private fun requireAuthToken(authToken: String?): String? {
        if (authToken.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Token is missing."
                )
            }
            return null
        }

        return authToken
    }
}