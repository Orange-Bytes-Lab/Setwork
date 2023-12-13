package com.designlife.justdo.common.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.designlife.justdo.common.data.entities.Category
import com.designlife.justdo.common.data.entities.Deck
import com.designlife.justdo.common.data.entities.Note
import com.designlife.justdo.common.data.entities.Todo
import com.designlife.justdo.common.data.room.dao.AppDatabase
import com.designlife.justdo.common.utils.security.EncryptionUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


suspend fun backupImport(context : Context,scope: CoroutineScope) : Boolean{
    val internalStorageDir = context.obbDir
    val fileDir = context.filesDir
    val externalFileDir = context.getExternalFilesDir(null)
    val envExternalFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    Log.i("BACKUP", "backupImport: internalStorageDir : ${internalStorageDir.absoluteFile}")
    val appFolder = File(envExternalFileDir, "Setwork")
    val backupFolder = File(appFolder, "Backup")

    if (!appFolder.exists() || !backupFolder.exists()) {
        Log.i("BACKUP", "backupExport: backupFolder not exists")
        return false
    }

    try {
        val backupTodoFile = File(backupFolder, AppDatabase.BACKUP_EN_TODO)
        val backupDeckFile = File(backupFolder, AppDatabase.BACKUP_EN_DECK)
        val backupCategoryFile = File(backupFolder, AppDatabase.BACKUP_EN_CATEGORY)
        val backupNoteFile = File(backupFolder, AppDatabase.BACKUP_EN_NOTE)

        val todoRepository = AppServiceLocator.provideTodoRepository(context)
        val deckRepository = AppServiceLocator.provideDeckRepository(context)
        val noteRepository = AppServiceLocator.provideNoteRepository(context)
        val categoryRepository = AppServiceLocator.provideCategoryRepository(context)

        scope.async(Dispatchers.IO) {
            val todoJson = backupTodoFile.readText()
            val deckJson = backupDeckFile.readText()
            val noteJson = backupNoteFile.readText()
            val categoryJson = backupCategoryFile.readText()

            Log.i("BACKUP", "backupImport: Todo Json : ${todoJson}")
            val todoListType = object : TypeToken<List<Todo>>() {}.type
            val todoList : List<Todo> = Gson().fromJson(async {decrypt(todoJson)}.await(),todoListType)

            val deckListType = object : TypeToken<List<Deck>>() {}.type
            val deckList : List<Deck> = Gson().fromJson(async {decrypt(deckJson)}.await(),deckListType)

            val noteListType = object : TypeToken<List<Note>>() {}.type
            val noteList : List<Note> = Gson().fromJson(async {decrypt(noteJson)}.await(),noteListType)

            val categoryListType = object : TypeToken<List<Category>>() {}.type
            val categoryList : List<Category> = Gson().fromJson(async {decrypt(categoryJson)}.await(),categoryListType)


            todoRepository.insertAllImportedTodo(todoList)
            deckRepository.insertAllImportedDeck(deckList)
            noteRepository.insertAllImportedNote(noteList)
            categoryRepository.insertAllImportedCategories(categoryList)
        }.await()

        return true
    } catch (e: IOException) {
        e.printStackTrace()
        Log.i("BACKUP", "backupImport: Exception : ${e.message}")
        return false
    }
}

suspend fun backupExport(context : Context,scope : CoroutineScope){
    val internalStorageDir = context.obbDir
    val fileDir = context.filesDir
    val externalFileDir = context.getExternalFilesDir(null).toString()
    val envExternalFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    Log.i("BACKUP", "backupExport: internalStorageDir : ${internalStorageDir.absoluteFile}")
    val appFolder = File(envExternalFileDir, "Setwork")
    if (!appFolder.exists()) {
        appFolder.mkdirs();
    }
    val backupFolder = File(appFolder, "Backup")
    if (!backupFolder.exists()) {
        backupFolder.mkdirs();
    }
    Log.i("BACKUP", "backupExport: backupFolder : ${backupFolder.absoluteFile}")
    val backupFileTodo = File(backupFolder, AppDatabase.BACKUP_EN_TODO)
    val backupFileNote = File(backupFolder, AppDatabase.BACKUP_EN_NOTE)
    val backupFileDeck = File(backupFolder, AppDatabase.BACKUP_EN_DECK)
    val backupFileCategory = File(backupFolder, AppDatabase.BACKUP_EN_CATEGORY)


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

        scope.async(Dispatchers.IO) {
            val todoFileWriter = FileWriter(backupFileTodo)
            todoFileWriter.use {
                Log.i("BACKUP", "backupExport: todo json content : ${todoJson}")
                it.write(async { encrypt(todoJson) }.await())
            }
            Log.i("BACKUP", "backupExport: todo file content : ${todoFileWriter.toString()}")

            val noteFileWriter = FileWriter(backupFileNote)
            noteFileWriter.use {
                it.write(async {encrypt(noteJson)}.await())
            }

            val deckFileWriter = FileWriter(backupFileDeck)
            deckFileWriter.use {
                it.write(async {encrypt(deckJson)}.await())
            }

            val categoryFileWriter = FileWriter(backupFileCategory)
            categoryFileWriter.use {
                it.write(async {encrypt(categoryJson)}.await())
            }

            setReadOnly(backupFileTodo)
            setReadOnly(backupFileNote)
            setReadOnly(backupFileDeck)
            setReadOnly(backupFileCategory)
        }.await()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private suspend fun encrypt(json : String) : String{
//    val secretKey = EncryptionUtils.generateKey()
//    return EncryptionUtils.encrypt(json,secretKey)
    return json
}

private suspend fun decrypt(json : String) : String{
//    val secretKey = EncryptionUtils.generateKey()
//    return EncryptionUtils.decrypt(json,secretKey)
    return json
}

private fun setReadOnly(file : File) = file.setReadOnly()