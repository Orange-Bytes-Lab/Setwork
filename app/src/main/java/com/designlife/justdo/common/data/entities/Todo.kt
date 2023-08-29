package com.designlife.justdo.common.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val todoId : Long = 0L,
    val title : String,
    val date : Long,
    val note : String,
    val isRepeated : Boolean,
    val repeatIndex : Int,
    val isCompleted : Boolean,
    val categoryId : Long,
    val createdOn : Long
)
