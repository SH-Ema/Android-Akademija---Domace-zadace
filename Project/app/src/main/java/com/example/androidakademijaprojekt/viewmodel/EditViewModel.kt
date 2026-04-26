package com.example.androidakademijaprojekt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidakademijaprojekt.model.Note
import com.example.androidakademijaprojekt.repository.NoteRepository

class EditViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    fun getNoteById(id: Int): Note? {
        return repository.getNoteById(id)
    }

    fun saveNote(id: Int?, title: String, content: String) {
        if (id == null) {
            repository.addNote(
                title = title,
                content = content
            )
        } else {
            repository.updateNote(
                id = id,
                title = title,
                content = content
            )
        }
    }
}
