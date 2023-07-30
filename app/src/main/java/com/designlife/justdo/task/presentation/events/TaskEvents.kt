package com.designlife.justdo.task.presentation.events

sealed class TaskEvents{
    data class OnTitleChange(val value : String) : TaskEvents()
    data class OnNoteChange(val value : String) : TaskEvents()
    data class OnDateChange(val value : String) : TaskEvents()
    data class OnTimeChange(val value : String) : TaskEvents()
}
