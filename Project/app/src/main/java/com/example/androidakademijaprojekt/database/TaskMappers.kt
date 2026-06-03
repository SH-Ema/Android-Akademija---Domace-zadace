package com.example.androidakademijaprojekt.database

import com.example.androidakademijaprojekt.network.model.TaskResponse

fun TaskResponse.toEntity(isSynced: Boolean = true): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        isSynced = isSynced
    )
}

fun TaskEntity.toResponse(): TaskResponse {
    return TaskResponse(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt
    )
}

fun List<TaskResponse>.toEntityList(): List<TaskEntity> {
    return map { taskResponse ->
        taskResponse.toEntity(isSynced = true)
    }
}

fun List<TaskEntity>.toResponseList(): List<TaskResponse> {
    return map { taskEntity ->
        taskEntity.toResponse()
    }
}