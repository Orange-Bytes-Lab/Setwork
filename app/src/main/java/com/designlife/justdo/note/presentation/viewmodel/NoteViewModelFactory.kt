package com.designlife.justdo.note.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.orchestrator.NotificationScheduler

@Suppress("UNCHECKED_CAST")
class NoteViewModelFactory(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository,
    private val notificationScheduler: NotificationScheduler


) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepository,categoryRepository,notificationScheduler) as T
    }
}