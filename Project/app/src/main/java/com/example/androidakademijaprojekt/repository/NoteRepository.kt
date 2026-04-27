package com.example.androidakademijaprojekt.repository

import com.example.androidakademijaprojekt.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteRepository {

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
        return formatter.format(Date())
    }

    private val notes = mutableListOf(
        Note(1, "Android Akademija", "Prvo", "26.04.2026.", "https://picsum.photos/300/300"),
        Note(2, "Uvod u Kotlin", "Drugo", "26.04.2026.", "https://picsum.photos/300/300"),
        Note(3, "Napredni Kotlin", "Treće", "26.04.2026.", "https://picsum.photos/300/300"),
        Note(4, "Uvod u Android", "Četvrto", "26.04.2026.", "https://picsum.photos/300/300"),
        Note(5, "Arhitektura", "Peto", "26.04.2026.", "https://picsum.photos/300/300")
    )

    fun getNotes(): List<Note> {
        return notes.toList()
    }

    fun getNoteById(id: Int): Note? {
        return notes.firstOrNull { note -> note.id == id
        }
    }

    fun addNote(title: String, content: String) {
        val newId = (notes.maxOfOrNull { note -> note.id } ?: 0) + 1

        val newNote = Note(
            id = newId,
            title = title,
            content = content,
            createdAt = getCurrentDate(),
            imageUrl = "https://picsum.photos/300/300"
        )

        notes.add(newNote)
    }

    fun updateNote(id: Int, title: String, content: String) {
        val index = notes.indexOfFirst { note -> note.id == id
        }

        if (index != -1) {
            val oldNote = notes[index]

            val updatedNote = oldNote.copy(
                title = title,
                content = content
            )

            notes[index] = updatedNote
        }
    }

    fun deleteNote(id: Int) {
        notes.removeAll { note ->
            note.id == id
        }
    }
}