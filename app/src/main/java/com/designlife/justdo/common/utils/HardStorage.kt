package com.designlife.justdo.common.utils

import android.content.Context
import android.util.Log
import com.designlife.justdo.common.data.entities.AppData
import com.designlife.justdo.common.data.entities.Category
import com.designlife.justdo.common.data.entities.Deck
import com.designlife.justdo.common.data.entities.Note
import com.designlife.justdo.common.data.entities.Todo
import com.designlife.justdo.common.data.room.dao.SetworkDatabase
import com.designlife.justdo.home.HomeFragment
import com.designlife.orchestrator.SchedulingEngine
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import java.io.FileWriter
import java.io.IOException


object HardStorage {
    suspend fun backupImport(context : Context,data : String) : Boolean {
        if (data.isEmpty()) return false
        try {
            val todoRepository = AppServiceLocator.provideTodoRepository(context)
            val deckRepository = AppServiceLocator.provideDeckRepository(context)
            val noteRepository = AppServiceLocator.provideNoteRepository(context)
            val categoryRepository = AppServiceLocator.provideCategoryRepository(context)

            val data = Gson().fromJson(data, AppData::class.java)

            data?.let {
                if (it.todos.isNotEmpty()){
                    val todos = it.todos.map { it.toTodo() }
                    todoRepository.insertAllImportedTodo(todos)
                    setNotifications(context,todos)
                }

                if (it.decks.isNotEmpty()){
                    deckRepository.insertAllImportedDeck(it.decks.map { it.toDeck() })
                }

                if (it.notes.isNotEmpty()){
                    noteRepository.insertAllImportedNote(it.notes.map { it.toNote() })
                }

                if (it.categories.isNotEmpty()){
                    categoryRepository.insertAllImportedCategories(it.categories.map { it.toCategory() })
                }
            } ?: return false

        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun setNotifications(context : Context,taskList : List<Todo>) {
        val notificationInfoData = taskList.filter { it.date >= System.currentTimeMillis() }.map { todo: Todo ->
            NotificationInfo(
                taskTitle = todo.title,
                taskSubTitle = (if (todo.note.isEmpty()) "" else if (todo.note.length > 25) "${
                    todo.note.substring(
                        0,
                        25
                    )
                } ..." else todo.note),
                scheduledTime = todo.date,
                createdTime = System.currentTimeMillis(),
                deliveredTime = 0L,
                notificationType = NotificationType.TASK_NOTIFY,
                notificationStatus = NotificationStatus.ACTIVE,
                taskId = todo.todoId.toInt()
            )
        }
        SchedulingEngine(context)
            .notificationScheduler()
            .scheduleBulkNotification(notificationInfoData)
    }

    fun backupExport(){
        try {
            val data = AppData(
                todos = HomeFragment.todoListIE,
                notes = HomeFragment.noteListIE,
                decks = HomeFragment.deckListIE,
                categories = HomeFragment.categoryListIE,
                createdAt = System.currentTimeMillis()
            )
            val dataJson = Gson().toJson(data)
            HomeFragment.EXPORT_DATA.value = dataJson
            Log.i("IE_FLOW", "backupExport: ${HomeFragment.EXPORT_DATA} ")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}