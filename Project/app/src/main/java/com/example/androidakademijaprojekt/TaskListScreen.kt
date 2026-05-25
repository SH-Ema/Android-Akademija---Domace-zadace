package com.example.androidakademijaprojekt

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onTaskLongClick: (TaskResponse) -> Unit,
    onRefreshClick: () -> Unit
) {
    var taskToDelete by remember {
        mutableStateOf<TaskResponse?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(35.dp)
    ) {
        Row {
            CustomButton(
                text = "Add task",
                onClick = onAddClick
            )

            Spacer(modifier = Modifier.padding(5.dp))

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
                            },
                            onLongClick = {
                                taskToDelete = task
                            }
                        )
                    }
                }
            }
        }
    }

    taskToDelete?.let { selectedTask ->
        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
            },
            title = {
                Text(text = "Delete task")
            },
            text = {
                Text(text = "Are you sure you want to delete \"${selectedTask.title}\"?")
            },
            confirmButton = {
                CustomButton(
                    text = "Delete",
                    onClick = {
                        onTaskLongClick(selectedTask)
                        taskToDelete = null
                    }
                )
            },
            dismissButton = {
                CustomButton(
                    text = "Cancel",
                    onClick = {
                        taskToDelete = null
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: TaskResponse,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
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