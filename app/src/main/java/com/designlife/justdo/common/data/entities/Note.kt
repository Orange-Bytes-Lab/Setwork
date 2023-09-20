package com.designlife.justdo.common.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId : Long = 0L,
    val title : String,
    val content : String,
    val emoji : String = "📓",
    val categoryId : Long,
    val coverImage : String = "",
    val createdTime: Long,
    val lastModified : Long,
)
