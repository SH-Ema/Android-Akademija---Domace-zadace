package com.example.androidakademijaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidakademijaprojekt.repository.TaskRepository

class EditTaskViewModelFactory(
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditTaskViewModel(taskRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}