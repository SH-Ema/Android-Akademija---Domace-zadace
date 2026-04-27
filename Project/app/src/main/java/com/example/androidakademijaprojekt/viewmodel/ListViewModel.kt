package com.example.androidakademijaprojekt.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.androidakademijaprojekt.model.Note
import com.example.androidakademijaprojekt.repository.NoteRepository

class ListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    var notes = mutableStateOf(repository.getNotes())
        private set

    fun shuffleNotes() {
        notes.value = notes.value.shuffled()
    }

    fun refreshNotes() {
        notes.value = repository.getNotes()
    }

    fun deleteNote(id: Int) {
        repository.deleteNote(id)
        refreshNotes()
    }
}