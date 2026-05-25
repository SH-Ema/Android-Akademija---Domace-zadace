package com.example.androidakademijaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidakademijaprojekt.repository.TaskRepository

class TaskListViewModelFactory(
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(taskRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}