package com.example.androidakademijaprojekt

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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
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

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.androidakademijaprojekt.model.Note
import com.example.androidakademijaprojekt.repository.NoteRepository
import com.example.androidakademijaprojekt.ui.theme.AndroidAkademijaProjektTheme
import com.example.androidakademijaprojekt.viewmodel.EditViewModel
import com.example.androidakademijaprojekt.viewmodel.ListViewModel


val noteRepository by lazy {
    NoteRepository()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AndroidAkademijaProjektTheme {


                val listViewModel = remember {
                    ListViewModel(noteRepository)
                }

                val editViewModel = remember {
                    EditViewModel(noteRepository)
                }


                val notes = listViewModel.notes.value

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "list"
                ) {
                    composable("list") {
                        ListScreen(
                            notes = notes,
                            onShuffleClick = {
                                listViewModel.shuffleNotes()
                            },
                            onAddClick = {
                                navController.navigate("edit/new")
                            },
                            onNoteClick = { selectedNote ->
                                navController.navigate("edit/${selectedNote.id}")
                            }
                        )
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

                        EditNoteScreen(
                            noteIdText = noteIdText,
                            editViewModel = editViewModel,
                            onBackClick = {
                                listViewModel.refreshNotes()
                                navController.popBackStack()
                            },
                            onSaveClick = {
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
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite icon",
                    tint = Color.Red,
                    modifier = Modifier.size(50.dp)
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
                CustomButton(text = "Fav")

                Spacer(modifier = Modifier.padding(5.dp))

                CustomButton(text = "Save")
            }
        }
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notes) { note ->
            NoteCard(
                note = note,
                onClick = {
                    onNoteClick(note)
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
    onNoteClick: (Note) -> Unit
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
            onNoteClick = onNoteClick
        )
    }
}

@Composable
fun EditNoteScreen(
    noteIdText: String?,
    editViewModel: EditViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val noteId = noteIdText?.toIntOrNull()

    val existingNote = noteId?.let { id ->
        editViewModel.getNoteById(id)
    }

    var title by remember(noteIdText) {
        mutableStateOf(existingNote?.title ?: "")
    }

    var content by remember(noteIdText) {
        mutableStateOf(existingNote?.content ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        TitleText(
            text = if (existingNote == null) "Nova bilješka" else "Uredi bilješku"
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = title,
            onValueChange = { newTitle ->
                title = newTitle
            },
            label = {
                Text("Naslov")
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
                Text("Opis")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Spremi",
            onClick = {
                editViewModel.saveNote(
                    id = noteId,
                    title = title,
                    content = content
                )

                onSaveClick()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomButton(
            text = "Back",
            onClick = onBackClick
        )
    }
}