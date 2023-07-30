package com.designlife.justdo.common.domain.entities

import androidx.compose.ui.graphics.Color


data class Category(
    val name : String,
    val totalTodo : Int,
    val totalCompleted : Int,
    val color : Color
)
