package com.designlife.justdo.note.presentation.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.AppOutput
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Category
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.utils.ImageConverter
import com.designlife.justdo.note.presentation.events.NoteEvents
import com.designlife.orchestrator.NotificationScheduler
import com.designlife.orchestrator.data.NotificationInfo
import com.designlife.orchestrator.data.NotificationStatus
import com.designlife.orchestrator.data.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.math.absoluteValue

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val TAG : String = this.javaClass.simpleName

    private val _noteId : MutableState<Long> = mutableStateOf(0L)

    private val _titleValue : MutableState<String> = mutableStateOf("")
    val titleValue = _titleValue

    private val _contentValue : MutableState<String> = mutableStateOf("")
    val contentValue = _contentValue

    private val _categoryId : MutableState<Long> = mutableStateOf(0L)
    val categoryId = _categoryId

    private val _coverImage : MutableState<Bitmap?> = mutableStateOf(null)
    val coverImage = _coverImage

    private val _createdTime : MutableState<Long> = mutableStateOf(0L)
    val createdTime = _createdTime

    private val _categoryList : MutableState<List<Category>> = mutableStateOf(listOf());
    val categoryList = _categoryList

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _selectedCategoryIndex : MutableState<Int> = mutableStateOf(-1);
    val selectedCategoryIndex = _selectedCategoryIndex

    private var _hasNoteModified : MutableState<Boolean> = mutableStateOf(false)
    val hasNoteModified  = _hasNoteModified

    private var _notePrevState = Triple<Note,Int,Bitmap?>(Note(),0,null)

    private val _progressBar : MutableState<Boolean> = mutableStateOf(false)
    val progressBar = _progressBar

    private val saveImageTimeMillis : Long = 200

    private val _threeDot : MutableState<Boolean> = mutableStateOf(false)
    val threeDot = _threeDot

    private val _aiChatState : MutableState<Boolean> = mutableStateOf(false)
    val aiChatState = _aiChatState

    private val _reminderState : MutableState<Boolean> = mutableStateOf(false)
    val reminderState = _reminderState

    private val _rawNoteDateTimeInstanceCompute : MutableState<Calendar> = mutableStateOf(Calendar.getInstance())
    private val _rawNoteDateTimeInstance : MutableState<Calendar> = mutableStateOf(Calendar.getInstance())
    val rawNoteDateTimeInstance = _rawNoteDateTimeInstance

    private val _selectedDateText : MutableState<String> = mutableStateOf(IDateGenerator.getGracefullyDateFromDate(
        Date(System.currentTimeMillis())
    ))
    val selectedDateText = _selectedDateText

    private val _selectedTimeText : MutableState<String> = mutableStateOf(IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis()))
    val selectedTimeText = _selectedTimeText

    private var _isUpdated : MutableState<Boolean> = mutableStateOf(false)

    private val _notifierAttached : MutableState<Boolean> = mutableStateOf(false)

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
            is NoteEvents.OnDeleteNote -> {
                deleteNoteById()
            }
            is NoteEvents.OnThreeDotToggle -> {
                _threeDot.value = event.state
            }
            is NoteEvents.OnAIChatToggle -> {
                _aiChatState.value = !_aiChatState.value
            }
            is NoteEvents.OnReminderToggle -> {
                _reminderState.value = !_reminderState.value
            }
            is NoteEvents.OnDateChange -> {
                val(day,month,year) = event.value.split("/")
                _selectedDateText.value = IDateGenerator.getGracefullyDateFrom(day.toInt(),month.toInt(),year.toInt())
                rawNoteDateTimeInstance.value.set(year.toInt(), month.toInt() - 1, day.toInt())
                _notifierAttached.value = true
            }
            is NoteEvents.OnTimeChange -> {
                val(hour,minute) = event.value.split(":")
                _selectedTimeText.value = IDateGenerator.getGracefullyTimeFrom(hour.toInt(),minute.toInt())
                _rawNoteDateTimeInstance.value.apply {
                    set(Calendar.HOUR_OF_DAY, hour.toInt())
                    set(Calendar.MINUTE, minute.toInt())
                }
                _notifierAttached.value = true
            }
            is NoteEvents.LoadNoteById -> {

            }
            is NoteEvents.OnDuplicateEvent -> {
                duplicateNote()
            }
            is NoteEvents.OnPdfExport -> {
                viewModelScope.launch(Dispatchers.IO) {
                    AppOutput.exportAsPdf(
                        context = event.context,
                        fileName = _titleValue.value,
                        content = _contentValue.value
                    )
                    withContext(Dispatchers.Main.immediate){
                        notificationScheduler.scheduleNotification(
                            NotificationInfo(
                                taskId = System.currentTimeMillis().absoluteValue.hashCode(),
                                scheduledTime = System.currentTimeMillis(),
                                taskTitle = "Setwork Document",
                                taskSubTitle = "${_titleValue.value}.pdf is available in downloads",
                                notificationType = NotificationType.COMMON_NOTIFY,
                                notificationStatus = NotificationStatus.DELIVERED
                            )
                        )
                    }

                }
            }
            is NoteEvents.OnPngExport -> {
                viewModelScope.launch(Dispatchers.IO) {
                    AppOutput.exportAsPng(
                        context = event.context,
                        fileName = _titleValue.value,
                        content = _contentValue.value
                    )
                    withContext(Dispatchers.Main.immediate){
                        notificationScheduler.scheduleNotification(
                            NotificationInfo(
                                taskId = System.currentTimeMillis().absoluteValue.hashCode(),
                                scheduledTime = System.currentTimeMillis(),
                                taskTitle = "Setwork Image",
                                taskSubTitle = "${_titleValue.value}.png is available in downloads",
                                notificationType = NotificationType.COMMON_NOTIFY,
                                notificationStatus = NotificationStatus.DELIVERED
                            )
                        )
                    }
                }
            }
        }
    }

    private fun deleteNoteById() {
        if (_noteId.value != -1L){
            try {
                viewModelScope.launch(Dispatchers.IO){
                    noteRepository.deleteNote(_noteId.value)
                }
            }catch (e : Exception){
                Log.e(TAG, "deleteNoteById: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun fetchNoteById(noteId : Long){
        _progressBar.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val note = noteRepository.getNoteById(noteId)
            withContext(Dispatchers.Main.immediate){
                note.also {
                    _titleValue.value = it.title
                    _contentValue.value = it.content
                    _noteId.value = it.noteId
                    _categoryId.value = it.categoryId
                }
                setNoteState(note)
                _progressBar.value = false
            }
            _coverImage.value = ImageConverter.getBitMapFromByteArray(note.coverImage)  // async(Dispatchers.Default) { }.await()
        }
    }

    private fun setNoteState(note: Note) {
        _notePrevState = Triple(note.copy(),_selectedCategoryIndex.value,_coverImage.value)
    }

    fun duplicateNote(){
        CoroutineScope(Dispatchers.IO).launch {
            val coverImage =  withContext(Dispatchers.Default) {
                ImageConverter.getByteArrayFromBitMap(_coverImage.value)
            }
            noteRepository.insertNote(Note(
                title = "Duplicate- " + _titleValue.value.ifEmpty { "Untitled" },
                content = _contentValue.value,
                categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                emoji = _categoryList.value[_selectedCategoryIndex.value].emoji,
                coverImage = coverImage,
                createdTime = Date(System.currentTimeMillis()),
                lastModified = Date(System.currentTimeMillis())
            ))
        }
    }

    fun insertNote(){
        if (_isUpdated.value) {
            updateNote()
            return
        }
        _isUpdated.value = true
        _progressBar.value = true
        _hasNoteModified.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val coverImage =  withContext(Dispatchers.Default) {
                ImageConverter.getByteArrayFromBitMap(_coverImage.value)
            }
            noteRepository.insertNote(Note(
                title = _titleValue.value.ifEmpty { "Untitled" },
                content = _contentValue.value,
                categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                emoji = _categoryList.value[_selectedCategoryIndex.value].emoji,
                coverImage = coverImage,
                createdTime = Date(System.currentTimeMillis()),
                lastModified = Date(System.currentTimeMillis())
            ))
            _hasNoteModified.value = false
            _progressBar.value = false
        }
        if (_rawNoteDateTimeInstanceCompute.value.equals(_rawNoteDateTimeInstance.value) == false){
            setNotification()
        }
    }

    fun updateNote() {
        if (isNoteUpdated()){
            _hasNoteModified.value = true
            CoroutineScope(Dispatchers.IO).launch {
                val coverImage =  withContext(Dispatchers.Default) {
                    ImageConverter.getByteArrayFromBitMap(_coverImage.value)
                }
                val note = Note(
                    noteId = _noteId.value,
                    title = _titleValue.value,
                    content = _contentValue.value,
                    emoji =_categoryList.value[_selectedCategoryIndex.value].emoji,
                    categoryId = _categoryList.value[_selectedCategoryIndex.value].id,
                    coverImage = coverImage,
                    createdTime = Date(_createdTime.value),
                    lastModified = Date(System.currentTimeMillis())
                )
                noteRepository.updateNote(
                    _noteId.value,
                    note
                )
                _hasNoteModified.value = false
            }
        }
        if (_rawNoteDateTimeInstanceCompute.value.equals(_rawNoteDateTimeInstance.value) == false && _notifierAttached.value){
            setNotification()
            _notifierAttached.value = false
        }
    }

    private fun isNoteUpdated(): Boolean {
        if (_notePrevState.second != _selectedCategoryIndex.value)
            return true
        _notePrevState.first.also {
            if (_titleValue.value != it.title)
                return true
            if (_contentValue.value != it.content)
                return true
        }
        if (_notePrevState.third != _coverImage.value)
            return true
        return false
    }

    suspend fun fetchCategories(){
        categoryRepository.getAllCategory().firstOrNull()?.let {
            _categoryList.value = it
        }
    }

    private fun setNotification() {
        val noteNotificationInfo = NotificationInfo(
            taskTitle = _titleValue.value.ifEmpty { "Setwork Note" },
            taskSubTitle = (if (_contentValue.value.isEmpty()) "" else if (_contentValue.value.length > 25) "${
                _contentValue.value.substring(
                    0,
                    25
                )
            } ..." else _contentValue.value),
            scheduledTime = _rawNoteDateTimeInstance.value.timeInMillis,
            createdTime = System.currentTimeMillis(),
            deliveredTime = 0L,
            notificationType = NotificationType.NOTE_NOTIFY,
            notificationStatus = NotificationStatus.ACTIVE,
            taskId = _noteId.value.toInt()
        )
        notificationScheduler.scheduleNotification(noteNotificationInfo)
    }

}