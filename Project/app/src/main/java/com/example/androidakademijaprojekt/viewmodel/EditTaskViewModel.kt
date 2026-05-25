package com.example.androidakademijaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidakademijaprojekt.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun prepareNewTask() {
        _uiState.value = EditTaskUiState()
    }

    fun loadTask(
        authToken: String?,
        taskId: String
    ) {
        val token = requireAuthToken(authToken) ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    taskId = taskId,
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val task = taskRepository.getTaskById(
                    authToken = token,
                    taskId = taskId
                )

                _uiState.update {
                    it.copy(
                        taskId = task.id,
                        title = task.title,
                        body = task.body,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load task."
                    )
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle,
                errorMessage = null
            )
        }
    }

    fun onBodyChange(newBody: String) {
        _uiState.update {
            it.copy(
                body = newBody,
                errorMessage = null
            )
        }
    }

    fun saveTask(authToken: String?) {
        val token = requireAuthToken(authToken) ?: return
        val currentState = _uiState.value

        if (currentState.title.isBlank() || currentState.body.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Title or body are empty.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            try {
                if (currentState.taskId == null) {
                    taskRepository.createTask(
                        authToken = token,
                        title = currentState.title,
                        body = currentState.body
                    )
                } else {
                    taskRepository.updateTask(
                        authToken = token,
                        taskId = currentState.taskId,
                        title = currentState.title,
                        body = currentState.body
                    )
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Failed to save task: ${exception.message}"
                    )
                }
            }
        }
    }

    fun deleteTask(authToken: String?) {
        val token = requireAuthToken(authToken) ?: return
        val taskId = _uiState.value.taskId

        if (taskId == null) {
            _uiState.update {
                it.copy(errorMessage = "The task does not exist.")
            }
            return
        }

        viewModelScope.launch {
            try {
                taskRepository.deleteTask(
                    authToken = token,
                    taskId = taskId
                )

                _uiState.update {
                    it.copy(
                        isDeleted = true,
                        errorMessage = null
                    )
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
                it.copy(errorMessage = "Token is missing.")
            }
            return null
        }

        return authToken
    }
}