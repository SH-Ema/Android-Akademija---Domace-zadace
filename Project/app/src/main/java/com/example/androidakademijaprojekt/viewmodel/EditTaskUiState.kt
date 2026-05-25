package com.example.androidakademijaprojekt.viewmodel

data class EditTaskUiState(
    val taskId: String? = null,
    val title: String = "",
    val body: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)