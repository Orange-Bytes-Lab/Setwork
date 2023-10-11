package com.designlife.justdo.note.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.note.presentation.events.NoteEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _noteId : MutableState<Long> = mutableStateOf(0L)

    private val _titleValue : MutableState<String> = mutableStateOf("")
    val titleValue = _titleValue

    private val _contentValue : MutableState<String> = mutableStateOf("")
    val contentValue = _contentValue

    private val _categoryId : MutableState<Long> = mutableStateOf(0L)
    val categoryId = _categoryId

    private val _coverImage : MutableState<String> = mutableStateOf("")
    val coverImage = _coverImage

    private val _createdTime : MutableState<Long> = mutableStateOf(0L)
    val createdTime = _createdTime

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _selectedCategoryIndex : MutableState<Int> = mutableStateOf(-1);
    val selectedCategoryIndex = _selectedCategoryIndex

    private var _hasDeckModified : MutableState<Boolean> = mutableStateOf(false)
    val hasDeckModified  = _hasDeckModified

    private var _notePrevState = Pair<Note,Int>(Note(),0)

    fun onEvent(event : NoteEvents){
        when(event){
            is NoteEvents.OnTitleChange -> {
                _titleValue.value = event.value
            }
            is NoteEvents.OnContentChange -> {
                _contentValue.value = event.value
            }
            is NoteEvents.OnCategoryChange -> {
                _categoryId.value = event.value
            }
            is NoteEvents.OnCoverChange -> {
                _coverImage.value = event.value
            }
            is NoteEvents.OnCategoryIndexChange -> {
                _selectedCategoryIndex.value = event.value
            }
        }
    }

    fun fetchNoteById(noteId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val note = noteRepository.getNoteById(noteId)
            note.also {
                _titleValue.value = it.title
                _contentValue.value = it.content
                _noteId.value = it.noteId
                _categoryId.value = it.categoryId
                _coverImage.value = it.coverImage
            }
            setNoteState(note)
        }
    }

    private fun setNoteState(note: Note) {
        _notePrevState = Pair(note.copy(),_selectedCategoryIndex.value)
    }

    fun insertNote(){
        if (_contentValue.value.isNotEmpty()){
            _hasDeckModified.value = true
            viewModelScope.launch(Dispatchers.IO) {
                noteRepository.insertTodo(Note(
                    title = _titleValue.value,
                    content = _contentValue.value,
                    categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                    emoji = _categoryList.value[_selectedCategoryIndex.value].emoji,
                    coverImage = _coverImage.value,
                    createdTime = Date(System.currentTimeMillis()),
                    lastModified = Date(System.currentTimeMillis())
                ))
                _hasDeckModified.value = false
            }
        }
    }

    fun updateNote() {
        Log.i("UPDATE_FLOW", "updateNote: Before isNoteUpdated")
        if (isNoteUpdated()){
            Log.i("UPDATE_FLOW", "updateNote: After isNoteUpdated")
            _hasDeckModified.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val note = Note(
                    noteId = _noteId.value,
                    title = _titleValue.value,
                    content = _contentValue.value,
                    emoji =_categoryList.value[_selectedCategoryIndex.value].emoji,
                    categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                    coverImage = _coverImage.value,
                    createdTime = Date(_createdTime.value),
                    lastModified = Date(System.currentTimeMillis())
                )
                noteRepository.updateNote(_noteId.value,note)
                _hasDeckModified.value = false
            }
        }


    }

    private fun isNoteUpdated(): Boolean {
        if (_selectedCategoryIndex.value != _notePrevState.second)
            return true
        _notePrevState.first.also {
            if (_coverImage.value != it.coverImage)
                return true
            if (_titleValue.value != it.title)
                return true
            if (_contentValue.value != it.content)
                return true
        }

        return false
    }

    suspend fun fetchCategories(){
        categoryRepository.getAllCategory().firstOrNull()?.let {
            _categoryList.value = it
        }
    }
}