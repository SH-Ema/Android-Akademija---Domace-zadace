package com.example.androidakademijaprojekt.viewmodel

import com.example.androidakademijaprojekt.network.model.TaskResponse

data class TaskListUiState(
    val tasks: List<TaskResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)