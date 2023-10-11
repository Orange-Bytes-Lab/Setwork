package com.designlife.justdo.common.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

data class Note(
    val noteId : Long = 0L,
    val title : String = "",
    val content : String = "",
    val emoji : String = "📓",
    val categoryId : Long = 0L,
    val coverImage : String = "",
    val createdTime: Date = Date(System.currentTimeMillis()),
    val lastModified : Date = Date(System.currentTimeMillis()),
)
