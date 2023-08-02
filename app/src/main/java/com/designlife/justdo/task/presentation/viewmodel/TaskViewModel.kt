package com.designlife.justdo.task.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.task.presentation.events.TaskEvents
import java.util.Calendar
import java.util.Date

class TaskViewModel : ViewModel() {

    private val _titleValue : MutableState<String> = mutableStateOf("")
    val titleValue = _titleValue

    private val _noteValue : MutableState<String> = mutableStateOf("")
    val noteValue = _noteValue

    private val _selectedDateText : MutableState<String> = mutableStateOf(IDateGenerator.getGracefullyDateFromDate(
        Date(System.currentTimeMillis())
    ))
    val selectedDateText = _selectedDateText

    private val _selectedTimeText : MutableState<String> = mutableStateOf(IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis()))
    val selectedTimeText = _selectedTimeText

    // Date , Time
    val _rawTaskDateTimeInstance = Calendar.getInstance()

    fun onEvent(event : TaskEvents){
        when(event){
            is TaskEvents.OnTitleChange -> {
                _titleValue.value = event.value
            }
            is TaskEvents.OnNoteChange -> {
                _noteValue.value = event.value
            }
            is TaskEvents.OnDateChange -> {
                val(day,month,year) = event.value.split("/")
                _selectedDateText.value = IDateGenerator.getGracefullyDateFrom(day.toInt(),month.toInt(),year.toInt())
                _rawTaskDateTimeInstance.set(year.toInt(), month.toInt() - 1, day.toInt())

            }
            is TaskEvents.OnTimeChange -> {
                val(hour,minute) = event.value.split(":")
                _selectedTimeText.value = IDateGenerator.getGracefullyTimeFrom(hour.toInt(),minute.toInt())
                _rawTaskDateTimeInstance.apply {
                    set(Calendar.HOUR_OF_DAY, hour.toInt())
                    set(Calendar.MINUTE, minute.toInt())
                }
            }
        }

    }

}