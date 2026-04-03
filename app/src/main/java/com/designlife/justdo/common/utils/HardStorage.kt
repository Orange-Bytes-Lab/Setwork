package com.designlife.justdo.common.utils

import android.content.Context
import android.util.Log
import com.designlife.justdo.common.data.entities.Category
import com.designlife.justdo.common.data.entities.Deck
import com.designlife.justdo.common.data.entities.Note
import com.designlife.justdo.common.data.entities.Todo
import com.designlife.justdo.common.data.room.dao.SetworkDatabase
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
    val scope = CoroutineScope(Dispatchers.IO + Job())
    suspend fun backupImport(context : Context) : Boolean {
        val backupFolder = File(context.getExternalFilesDir(null),"Setwork/Backup/")
//        val envExternalFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        if (!backupFolder.exists()){
            return false
        }
        try {
            val backupTodoFile = File(backupFolder,SetworkDatabase.BACKUP_EN_TODO)
            val backupDeckFile = File(backupFolder,SetworkDatabase.BACKUP_EN_DECK)
            val backupCategoryFile = File(backupFolder,SetworkDatabase.BACKUP_EN_CATEGORY)
            val backupNoteFile = File(backupFolder, SetworkDatabase.BACKUP_EN_NOTE)

            setFilePermission(backupTodoFile)
            setFilePermission(backupDeckFile)
            setFilePermission(backupCategoryFile)
            setFilePermission(backupNoteFile)

            val todoRepository = AppServiceLocator.provideTodoRepository(context)
            val deckRepository = AppServiceLocator.provideDeckRepository(context)
            val noteRepository = AppServiceLocator.provideNoteRepository(context)
            val categoryRepository = AppServiceLocator.provideCategoryRepository(context)

            val todoJson = backupTodoFile.readText()
            val deckJson = backupDeckFile.readText()
            val noteJson = backupNoteFile.readText()
            val categoryJson = backupCategoryFile.readText()

            val todoListType = object : TypeToken<List<Todo>>() {}.type
            val todoList : List<Todo> = Gson().fromJson(scope.async {decrypt(todoJson)}.await(),todoListType)

            val deckListType = object : TypeToken<List<Deck>>() {}.type
            val deckList : List<Deck> = Gson().fromJson(scope.async {decrypt(deckJson)}.await(),deckListType)

            val noteListType = object : TypeToken<List<Note>>() {}.type
            val noteList : List<Note> = Gson().fromJson(scope.async {decrypt(noteJson)}.await(),noteListType)

            val categoryListType = object : TypeToken<List<Category>>() {}.type
            val categoryList : List<Category> = Gson().fromJson(scope.async {decrypt(categoryJson)}.await(),categoryListType)


            todoRepository.insertAllImportedTodo(todoList)
            deckRepository.insertAllImportedDeck(deckList)
            noteRepository.insertAllImportedNote(noteList)
            categoryRepository.insertAllImportedCategories(categoryList)
            setNotifications(context,todoList)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun setNotifications(context : Context,taskList : List<Todo>) {
        val notificationInfoData = taskList.map { todo: Todo ->
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

    suspend fun backupExport(context : Context){
        val backupFolder = File(context.getExternalFilesDir(null),"Setwork/Backup/")
        if (!backupFolder.exists()){
            backupFolder.mkdirs()
        }

        val backupFileTodo = File(backupFolder,SetworkDatabase.BACKUP_EN_TODO)
        val backupFileNote = File(backupFolder,SetworkDatabase.BACKUP_EN_NOTE)
        val backupFileDeck = File(backupFolder, SetworkDatabase.BACKUP_EN_DECK)
        val backupFileCategory = File(backupFolder,  SetworkDatabase.BACKUP_EN_CATEGORY)

        checkFileExists(backupFileTodo)
        checkFileExists(backupFileNote)
        checkFileExists(backupFileDeck)
        checkFileExists(backupFileCategory)

        setFilePermission(backupFileTodo)
        setFilePermission(backupFileNote)
        setFilePermission(backupFileDeck)
        setFilePermission(backupFileCategory)

        try {
            val todoRepository = AppServiceLocator.provideTodoRepository(context)
            val deckRepository = AppServiceLocator.provideDeckRepository(context)
            val noteRepository = AppServiceLocator.provideNoteRepository(context)
            val categoryRepository = AppServiceLocator.provideCategoryRepository(context)
            val todoList =  todoRepository.getAllRawTodo().firstOrNull()
            val deckList = deckRepository.getAllRawDecks().firstOrNull()
            val noteList = noteRepository.getAllRawNotes().firstOrNull()
            val categoryList = categoryRepository.getAllRawCategory().firstOrNull()

            val todoJson = Gson().toJson(todoList)
            val deckJson = Gson().toJson(deckList)
            val noteJson = Gson().toJson(noteList)
            val categoryJson = Gson().toJson(categoryList)

            val todoFileWriter = FileWriter(backupFileTodo, false)
            todoFileWriter.use {
                it.write(scope.async { encrypt(todoJson) }.await())
            }


            val noteFileWriter = FileWriter(backupFileNote, false)
            noteFileWriter.use {
                it.write(scope.async {encrypt(noteJson)}.await())
            }

            val deckFileWriter = FileWriter(backupFileDeck, false)
            deckFileWriter.use {
                it.write(scope.async {encrypt(deckJson)}.await())
            }

            val categoryFileWriter = FileWriter(backupFileCategory, false)
            categoryFileWriter.use {
                it.write(scope.async {encrypt(categoryJson)}.await())
            }

            todoFileWriter.close()
            noteFileWriter.close()
            deckFileWriter.close()
            categoryFileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkFileExists(backupFile: File) {
        if (!backupFile.exists()){
            backupFile.createNewFile()
        }
    }

    private fun setFilePermission(backupFile: File) {
        backupFile.setExecutable(true)
        backupFile.setWritable(true)
        backupFile.setReadable(true)
    }

    private suspend fun encrypt(json : String) : String{
    return json
}

    private suspend fun decrypt(json : String) : String{
        return json
    }

    private fun setReadOnly(file : File) = file.setReadOnly()

    fun clear(){
        scope?.cancel()
    }

}