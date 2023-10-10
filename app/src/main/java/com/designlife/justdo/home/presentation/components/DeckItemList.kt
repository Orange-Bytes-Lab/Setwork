package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.ui.theme.TaskItemLabelColor

@Composable
fun DeckItemList(
    listState: LazyListState,
    deckList: List<Deck>,
    colorMap: Map<Long, Color>,
    onDeckClickEvent: (index: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        itemsIndexed(
            items = deckList,
            contentType = { index, deck -> deck.categoryId },
            key = { index, deck -> deck.deckId },
        ) { index, deck ->
            deck.let { item ->
                if (colorMap.containsKey(item.categoryId)) {
                    val color: Color = colorMap.get(item.categoryId) ?: TaskItemLabelColor
                    DeckItem(
                        deck = item,
                        deckTheme = color
                    ) {
                        onDeckClickEvent(index)
                    }
                }
            }
        }
    }
}