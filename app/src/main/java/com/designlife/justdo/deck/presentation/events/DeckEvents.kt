package com.designlife.justdo.deck.presentation.events

import com.designlife.justdo.common.domain.entities.FlashCard

sealed class DeckEvents{
    data class OnHeaderChange(val value : String) : DeckEvents()
    data class OnCardRemove(val index : Int) : DeckEvents()
    object OnDeckToggle : DeckEvents()
    object OnCreateCard : DeckEvents()
    data class OnEditStateChange(val editState : Boolean) : DeckEvents()
    data class OnUpdateCardChange(val index : Int,val card : FlashCard) : DeckEvents()
    object OnPersistCardChanges : DeckEvents()
}
