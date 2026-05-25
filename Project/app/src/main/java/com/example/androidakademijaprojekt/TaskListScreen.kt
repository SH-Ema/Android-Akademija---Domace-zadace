package com.example.androidakademijaprojekt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidakademijaprojekt.network.model.TaskResponse
import com.example.androidakademijaprojekt.viewmodel.TaskListUiState

@Composable
fun TaskListScreen(
    uiState: TaskListUiState,
    onAddClick: () -> Unit,
    onTaskClick: (TaskResponse) -> Unit,
    onRefreshClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Row {
            CustomButton(
                text = "Add task",
                onClick = onAddClick
            )

            Spacer(modifier = Modifier.padding(15.dp))

            CustomButton(
                text = "Refresh",
                onClick = onRefreshClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            uiState.errorMessage != null -> {
                Text(text = uiState.errorMessage)
            }

            uiState.tasks.isEmpty() -> {
                Text(text = "No tasks found.")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.tasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = {
                                onTaskClick(task)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            TitleText(text = task.title)

            Spacer(modifier = Modifier.height(10.dp))

            DescriptionText(text = task.body)

            task.createdAt?.let { date ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Date: $date")
            }
        }
    }
}