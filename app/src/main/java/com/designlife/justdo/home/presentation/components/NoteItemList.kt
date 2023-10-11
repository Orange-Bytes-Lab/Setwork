package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.common.domain.entities.Note
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItemList(
    listState : LazyStaggeredGridState,
    noteList : List<Note>,
    colorMap: Map<Long, Color>,
    onNoteClickEvent : (index : Int) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .fillMaxSize(),
        verticalItemSpacing = 8.dp,
        columns = StaggeredGridCells.Fixed(2),
        state = listState
    ){
        items(noteList.size){ index: Int ->
            val item = noteList[index]
            if (colorMap.containsKey(item.categoryId)) {
                val color: Color = colorMap.get(item.categoryId) ?: TaskItemLabelColor
                NoteItem(note = noteList[index], noteTheme = color) {
                    onNoteClickEvent(index)
                }
            }
        }
    }
}