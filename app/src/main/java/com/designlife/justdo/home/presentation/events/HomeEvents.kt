package com.designlife.justdo.home.presentation.events

sealed class HomeEvents{
    data class OnIndexSelected(val index : Int) : HomeEvents()
    data class OnTodoEvent(val index : Int) : HomeEvents()
    data class HighlightTodoByDate(val visibleIndex : Int) : HomeEvents()
    data class HighlightDateByTodo(val visibleIndex : Int) : HomeEvents()
    data class OnCategorySortSelected(val categoryIndex : Int) : HomeEvents()
}
