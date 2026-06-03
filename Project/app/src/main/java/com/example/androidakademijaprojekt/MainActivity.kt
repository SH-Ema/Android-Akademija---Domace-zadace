package com.example.androidakademijaprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidakademijaprojekt.repository.AuthRepository
import com.example.androidakademijaprojekt.ui.theme.AndroidAkademijaProjektTheme
import com.example.androidakademijaprojekt.viewmodel.EditTaskViewModel
import com.example.androidakademijaprojekt.viewmodel.LoginViewModel
import com.example.androidakademijaprojekt.viewmodel.TaskListViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidAkademijaProjektTheme {
                val authRepository: AuthRepository = koinInject()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        val loginViewModel: LoginViewModel = koinViewModel()
                        val loginUiState by loginViewModel.uiState.collectAsState()

                        LaunchedEffect(loginUiState.isLoggedIn) {
                            if (loginUiState.isLoggedIn) {
                                navController.navigate("list") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                        LoginScreen(
                            uiState = loginUiState,
                            onUsernameChange = loginViewModel::onUsernameChange,
                            onPasswordChange = loginViewModel::onPasswordChange,
                            onLoginClick = loginViewModel::login
                        )
                    }

                    composable("list") {
                        val taskListViewModel: TaskListViewModel = koinViewModel()
                        val taskListUiState by taskListViewModel.uiState.collectAsState()

                        LaunchedEffect(authRepository.authToken) {
                            taskListViewModel.loadTasks(authRepository.authToken)
                        }

                        TaskListScreen(
                            uiState = taskListUiState,
                            onAddClick = { navController.navigate("edit/new") },
                            onVolleyballClick = { navController.navigate("edit/volleyball") },
                            onTaskClick = { selectedTask -> navController.navigate("edit/${selectedTask.id}") },
                            onTaskLongClick = { selectedTask ->
                                taskListViewModel.deleteTask(
                                    authToken = authRepository.authToken,
                                    taskId = selectedTask.id
                                )
                            }
                        )
                    }

                    composable(
                        route = "edit/{taskId}",
                        arguments = listOf(
                            navArgument("taskId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val taskIdText = backStackEntry.arguments?.getString("taskId")
                        val isVolleyballTask = taskIdText == "volleyball"

                        val taskId = if (taskIdText == "new" || isVolleyballTask) {
                            null
                        } else {
                            taskIdText
                        }

                        val editTaskViewModel: EditTaskViewModel = koinViewModel()
                        val editTaskUiState by editTaskViewModel.uiState.collectAsState()

                        LaunchedEffect(taskIdText, authRepository.authToken) {
                            if (isVolleyballTask) {
                                editTaskViewModel.prepareNewVolleyballTask()
                            } else if (taskId == null) {
                                editTaskViewModel.prepareNewTask()
                            } else {
                                editTaskViewModel.loadTask(
                                    authToken = authRepository.authToken,
                                    taskId = taskId
                                )
                            }
                        }

                        LaunchedEffect(
                            editTaskUiState.isSaved,
                            editTaskUiState.isDeleted
                        ) {
                            if (editTaskUiState.isSaved || editTaskUiState.isDeleted) {
                                navController.navigate("list") {
                                    popUpTo("list") {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                        EditTaskScreen(
                            uiState = editTaskUiState,
                            onTitleChange = editTaskViewModel::onTitleChange,
                            onBodyChange = editTaskViewModel::onBodyChange,
                            onTrainingSuggestionClick = editTaskViewModel::addTrainingSuggestion,
                            onDoneClick = { editTaskViewModel.saveTask(authRepository.authToken) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4C0F96)
    )
}

@Composable
fun DescriptionText(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontStyle = FontStyle.Italic,
        color = Color.Black,
        maxLines = 3
    )
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF7F2FA),
            contentColor = Color(0xFF4C0F96)
        )
    ) {
        Text(
            text = text,
            color = Color(0xFF4C0F96)
        )
    }
}