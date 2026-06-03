package com.example.androidakademijaprojekt.viewmodel

import com.example.androidakademijaprojekt.logger.AppLogger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidakademijaprojekt.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val logger: AppLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState = _uiState.asStateFlow()

    private fun todayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    fun prepareNewTask() {
        _uiState.value = EditTaskUiState(
            createdAt = todayDate()
        )
    }

    fun prepareNewVolleyballTask() {
        _uiState.value = EditTaskUiState(
            title = "🏐 Volleyball training",
            body = "Warm-up:\n\nDrills:\n\nCool-down:\n",
            createdAt = todayDate()
        )
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
                        createdAt = task.createdAt ?: todayDate(),
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

    fun addTrainingSuggestion(sectionTitle: String, suggestion: String) {
        _uiState.update { currentState ->
            currentState.copy(
                body = addSuggestionToSection(
                    body = currentState.body,
                    sectionTitle = sectionTitle,
                    suggestion = suggestion
                ),
                errorMessage = null
            )
        }
    }

    private fun addSuggestionToSection(
        body: String,
        sectionTitle: String,
        suggestion: String
    ): String {
        val sectionHeader = "$sectionTitle:"
        val suggestionLine = "- $suggestion"

        if (body.contains(suggestionLine)) {
            return body
        }

        if (!body.contains(sectionHeader)) {
            return body.trimEnd() + "\n\n$sectionHeader\n$suggestionLine\n"
        }

        val allSectionHeaders = listOf(
            "Warm-up:",
            "Drills:",
            "Cool-down:"
        )

        val sectionStartIndex = body.indexOf(sectionHeader)
        val contentStartIndex = sectionStartIndex + sectionHeader.length

        val nextSectionIndex = allSectionHeaders
            .filter { it != sectionHeader }
            .map { body.indexOf(it, startIndex = contentStartIndex) }
            .filter { it != -1 }
            .minOrNull()

        val insertIndex = nextSectionIndex ?: body.length

        val textBeforeInsert = body.substring(0, insertIndex).trimEnd()
        val textAfterInsert = body.substring(insertIndex).trimStart()

        return if (textAfterInsert.isBlank()) {
            "$textBeforeInsert\n$suggestionLine\n"
        } else {
            "$textBeforeInsert\n$suggestionLine\n\n$textAfterInsert"
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