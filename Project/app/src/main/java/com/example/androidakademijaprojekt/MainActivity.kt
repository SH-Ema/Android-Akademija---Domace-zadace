package com.example.androidakademijaprojekt

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.example.androidakademijaprojekt.model.Note
import com.example.androidakademijaprojekt.repository.AuthRepository
import com.example.androidakademijaprojekt.repository.NoteRepository
import com.example.androidakademijaprojekt.repository.TaskRepository
import com.example.androidakademijaprojekt.ui.theme.AndroidAkademijaProjektTheme
import com.example.androidakademijaprojekt.viewmodel.EditViewModel
import com.example.androidakademijaprojekt.viewmodel.ListViewModel
import com.example.androidakademijaprojekt.viewmodel.LoginViewModel
import com.example.androidakademijaprojekt.viewmodel.LoginViewModelFactory
import com.example.androidakademijaprojekt.viewmodel.TaskListViewModel
import com.example.androidakademijaprojekt.viewmodel.TaskListViewModelFactory

val noteRepository by lazy { NoteRepository() }

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

                val listViewModel = remember { ListViewModel(noteRepository) }
                val editViewModel = remember { EditViewModel(noteRepository) }

                val notes = listViewModel.notes.value
                val navController = rememberNavController()

                val context = LocalContext.current
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
                                onAddClick = {
                                    navController.navigate("edit/new")
                                },
                                onTaskClick = { selectedTask ->
                                    navController.navigate("edit/${selectedTask.id}")
                                },
                                onRefreshClick = {
                                    taskListViewModel.loadTasks(authRepository.authToken)
                                }
                            )
                        }

                        composable(
                            route = "detail/{noteId}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getInt("noteId")

                            if (noteId == null || noteId < 0) {
                                InvalidNoteScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                val selectedNote = notes.firstOrNull { note ->
                                    note.id == noteId
                                }

                                NoteDetailScreen(
                                    note = selectedNote,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable(
                            route = "edit/{noteId}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val noteIdText = backStackEntry.arguments?.getString("noteId")
                            val noteId = noteIdText?.toIntOrNull()

                            val existingNote = noteId?.let { id ->
                                editViewModel.getNoteById(id)
                            }

                            EditNoteScreen(
                                note = existingNote,
                                onBackClick = {
                                    listViewModel.refreshNotes()
                                    navController.popBackStack()
                                },
                                onSaveClick = { title, content ->
                                    editViewModel.saveNote(
                                        id = noteId,
                                        title = title,
                                        content = content
                                    )

                                    listViewModel.refreshNotes()
                                    navController.popBackStack()
                                }
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
fun NoteCard(
    note: Note,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onCardClick()
            },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubcomposeAsyncImage(
                    model = note.imageUrl,
                    contentDescription = "Note picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    error = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    success = {
                        SubcomposeAsyncImageContent()
                    }
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Column {
                    TitleText(text = note.title)

                    Spacer(modifier = Modifier.height(10.dp))

                    DescriptionText(text = note.content)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row {
                CustomButton(
                    text = "Edit",
                    onClick = onEditClick
                )

                Spacer(modifier = Modifier.padding(5.dp))

                CustomButton(
                    text = "Delete",
                    onClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notes) { note ->
            NoteCard(
                note = note,
                onCardClick = {
                    onNoteClick(note)
                },
                onEditClick = {
                    onEditClick(note)
                },
                onDeleteClick = {
                    onDeleteClick(note)
                }
            )
        }
    }
}

@Composable
fun ListScreen(
    notes: List<Note>,
    onShuffleClick: () -> Unit,
    onAddClick: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(35.dp)
    ) {
        CustomButton(
            text = "Add note",
            onClick = onAddClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomButton(
            text = "Shuffle",
            onClick = onShuffleClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        NoteList(
            notes = notes,
            onNoteClick = onNoteClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
fun NoteDetailScreen(
    note: Note?,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        if (note == null) {
            TitleText(text = "Note not found")

            Spacer(modifier = Modifier.height(15.dp))

            DescriptionText(text = "Error: ID not recognised.")
        } else {
            SubcomposeAsyncImage(
                model = note.imageUrl,
                contentDescription = "Note picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(15.dp)),
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    CircularProgressIndicator()
                },
                success = {
                    SubcomposeAsyncImageContent()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TitleText(text = note.title)

            Spacer(modifier = Modifier.height(15.dp))

            DescriptionText(text = note.content)

            Spacer(modifier = Modifier.height(15.dp))

            Text(text = "Date: ${note.createdAt}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}

@Composable
fun InvalidNoteScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        TitleText(text = "Error")

        Spacer(modifier = Modifier.height(15.dp))

        DescriptionText(text = "Error: ID not recognised.")

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Back",
            onClick = onBackClick
        )
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

@Composable
fun EditNoteScreen(
    note: Note?,
    onBackClick: () -> Unit,
    onSaveClick: (title: String, content: String) -> Unit
) {
    var title by remember(note?.id) {
        mutableStateOf(note?.title ?: "")
    }

    var content by remember(note?.id) {
        mutableStateOf(note?.content ?: "")
    }

    val createdAt = note?.createdAt ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        TitleText(
            text = if (note == null) "New note" else "Edit note"
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = title,
            onValueChange = { newTitle ->
                title = newTitle
            },
            label = {
                Text("Title")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = content,
            onValueChange = { newContent ->
                content = newContent
            },
            label = {
                Text("Description")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = createdAt,
            onValueChange = {},
            label = {
                Text("Date")
            },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Save",
            onClick = {
                onSaveClick(title, content)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}