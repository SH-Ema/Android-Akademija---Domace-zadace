package com.example.androidakademijaprojekt

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidakademijaprojekt.repository.AuthRepository
import com.example.androidakademijaprojekt.repository.TaskRepository
import com.example.androidakademijaprojekt.ui.theme.AndroidAkademijaProjektTheme
import com.example.androidakademijaprojekt.viewmodel.EditTaskViewModel
import com.example.androidakademijaprojekt.viewmodel.EditTaskViewModelFactory
import com.example.androidakademijaprojekt.viewmodel.LoginViewModel
import com.example.androidakademijaprojekt.viewmodel.LoginViewModelFactory
import com.example.androidakademijaprojekt.viewmodel.TaskListViewModel
import com.example.androidakademijaprojekt.viewmodel.TaskListViewModelFactory

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidAkademijaProjektTheme {
                val authRepository = remember { AuthRepository() }
                val taskRepository = remember { TaskRepository() }

                val navController = rememberNavController()

                val context = this@MainActivity
                var hasInternet by remember { mutableStateOf(isInternetAvailable(context)) }

                if (!hasInternet) {
                    NoInternetScreen(
                        onRetryClick = {
                            hasInternet = isInternetAvailable(context)
                        }
                    )
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = LoginViewModelFactory(authRepository)
                            )

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
                            val taskListViewModel: TaskListViewModel = viewModel(
                                factory = TaskListViewModelFactory(taskRepository)
                            )

                            val taskListUiState by taskListViewModel.uiState.collectAsState()

                            LaunchedEffect(authRepository.authToken) {
                                taskListViewModel.loadTasks(authRepository.authToken)
                            }

                            TaskListScreen(
                                uiState = taskListUiState,
                                onAddClick = { navController.navigate("edit/new") },
                                onTaskClick = { selectedTask ->
                                    navController.navigate("edit/${selectedTask.id}")
                                },
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
                            val taskId = if (taskIdText == "new") {
                                null
                            } else {
                                taskIdText
                            }

                            val editTaskViewModel: EditTaskViewModel = viewModel(
                                factory = EditTaskViewModelFactory(taskRepository)
                            )

                            val editTaskUiState by editTaskViewModel.uiState.collectAsState()

                            LaunchedEffect(taskId, authRepository.authToken) {
                                if (taskId == null) {
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
                                onDoneClick = { editTaskViewModel.saveTask(authRepository.authToken) },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
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
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun NoInternetScreen(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleText(text = "No internet")

        Spacer(modifier = Modifier.height(20.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        DescriptionText(text = "Check your connection and try again.")

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Retry",
            onClick = onRetryClick
        )
    }
}