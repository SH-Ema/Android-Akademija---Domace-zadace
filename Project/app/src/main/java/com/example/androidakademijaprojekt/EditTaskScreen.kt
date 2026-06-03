package com.example.androidakademijaprojekt

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.androidakademijaprojekt.viewmodel.EditTaskUiState

@Composable
fun EditTaskScreen(
    uiState: EditTaskUiState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onTrainingSuggestionClick: (String, String) -> Unit,
    onDoneClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(20.dp)
    ) {
        TitleText(
            text = if (uiState.taskId == null) {
                "New task"
            } else {
                "Edit task"
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            TextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = {
                    Text("Title")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = uiState.body,
                onValueChange = onBodyChange,
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            if (uiState.title.startsWith("🏐")) {
                Spacer(modifier = Modifier.height(15.dp))

                VolleyballSuggestionSection(
                    sectionTitle = "Warm-up",
                    suggestions = listOf(
                        "Dynamic stretching",
                        "Pepper drill",
                        "Shoulder mobility"
                    ),
                    onSuggestionClick = onTrainingSuggestionClick
                )

                VolleyballSuggestionSection(
                    sectionTitle = "Drills",
                    suggestions = listOf(
                        "Serve receive",
                        "Attack approach",
                        "Blocking footwork"
                    ),
                    onSuggestionClick = onTrainingSuggestionClick
                )

                VolleyballSuggestionSection(
                    sectionTitle = "Cool-down",
                    suggestions = listOf(
                        "Static stretching",
                        "Breathing exercises",
                        "Team feedback"
                    ),
                    onSuggestionClick = onTrainingSuggestionClick
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = uiState.createdAt,
                onValueChange = {},
                label = { Text("Date") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isSaving) {
                CircularProgressIndicator()
            } else {
                CustomButton(
                    text = "Done",
                    onClick = onDoneClick
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            CustomButton(
                text = "Back",
                onClick = onBackClick
            )

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = message,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun VolleyballSuggestionSection(
    sectionTitle: String,
    suggestions: List<String>,
    onSuggestionClick: (String, String) -> Unit
) {
    Text(text = "$sectionTitle ideas")

    Spacer(modifier = Modifier.height(10.dp))

    suggestions.forEach { suggestion ->
        CustomButton(
            text = suggestion,
            onClick = {
                onSuggestionClick(sectionTitle, suggestion)
            }
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
}