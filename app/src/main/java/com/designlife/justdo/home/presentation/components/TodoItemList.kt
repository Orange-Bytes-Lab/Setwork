package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun TodoItemList(
    listState : LazyListState,
    todoList : List<Todo>,
    colorMap : Map<Long,Color>,
    onFirstIndexChangeEvent : (index : Int) -> Unit,
    onTodoClickEvent : () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ){
        items(todoList.size){ index->
            todoList.get(index).let {item->
                if (colorMap.containsKey(item.categoryId)){
                    val color : Color = colorMap.get(item.categoryId) ?: TaskItemLabelColor
                    TodoItem(
                        color = color,
                        todo = item
                    ){
                        onTodoClickEvent()
                    }
                    LaunchedEffect(listState.firstVisibleItemIndex){
                        onFirstIndexChangeEvent(listState.firstVisibleItemIndex)
                    }
                }
            }
        }
    }
}