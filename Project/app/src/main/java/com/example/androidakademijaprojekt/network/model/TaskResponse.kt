package com.example.androidakademijaprojekt.network.model

data class TaskResponse(
    val id: Int,
    val title: String,
    val body: String,
    val createdAt: String? = null
)