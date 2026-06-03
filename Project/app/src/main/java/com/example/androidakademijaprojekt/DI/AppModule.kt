package com.example.androidakademijaprojekt.DI

import com.example.androidakademijaprojekt.database.TaskDatabase
import com.example.androidakademijaprojekt.logger.AppLogger
import com.example.androidakademijaprojekt.network.RetrofitInstance
import com.example.androidakademijaprojekt.repository.AuthRepository
import com.example.androidakademijaprojekt.repository.TaskRepository
import com.example.androidakademijaprojekt.viewmodel.EditTaskViewModel
import com.example.androidakademijaprojekt.viewmodel.LoginViewModel
import com.example.androidakademijaprojekt.viewmodel.TaskListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        AppLogger(tag = "AndroidAkademija")
    }

    single {
        RetrofitInstance.api
    }

    single {
        AuthRepository(
            api = get(),
            logger = get()
        )
    }

    single {
        TaskDatabase.getDatabase(
            context = androidContext()
        )
    }

    single {
        get<TaskDatabase>().taskDao()
    }

    single {
        TaskRepository(
            api = get(),
            taskDao = get(),
            logger = get()
        )
    }

    viewModel {
        LoginViewModel(
            authRepository = get(),
            logger = get()
        )
    }

    viewModel {
        TaskListViewModel(
            taskRepository = get(),
            logger = get()
        )
    }

    viewModel {
        EditTaskViewModel(
            taskRepository = get(),
            logger = get()
        )
    }
}