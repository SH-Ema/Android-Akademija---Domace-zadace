package com.example.androidakademijaprojekt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidakademijaprojekt.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadTasks(authToken: String?) {
        if (authToken.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Auth token is missing."
                )
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

            try {
                val tasks = taskRepository.getAllTasks(authToken)

                Log.d("TASK_DEBUG", "Tasks loaded: $tasks")

                _uiState.update {
                    it.copy(
                        tasks = tasks,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (exception: HttpException) {
                val errorBody = exception.response()?.errorBody()?.string()

                Log.e(
                    "TASK_DEBUG",
                    "HTTP error: ${exception.code()} - $errorBody"
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load. HTTP ${exception.code()}"
                    )
                }
            } catch (exception: Exception) {
                Log.e(
                    "TASK_DEBUG",
                    "Other error: ${exception.message}",
                    exception
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load: ${exception.message}"
                    )
                }
            }
        }
    }
}