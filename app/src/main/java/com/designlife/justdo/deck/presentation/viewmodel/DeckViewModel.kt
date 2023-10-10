package com.designlife.justdo.deck.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.common.domain.calendar.IDateGenerator
import com.designlife.justdo.common.domain.entities.Deck
import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.deck.presentation.events.DeckEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DeckViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _deckId : MutableState<Long> = mutableStateOf(0L)

    private val _headerTitle : MutableState<String> = mutableStateOf("")
    val headerTitle = _headerTitle

    private val _modifiedTime : MutableState<Long> = mutableStateOf(0L)
    val modifiedTime = _modifiedTime

    private val _cardList : MutableState<MutableList<FlashCard>> = mutableStateOf(mutableListOf());
    val cardList = _cardList

    private var _deckToggle : MutableState<Boolean> = mutableStateOf(false)
    val deckToggle  = _deckToggle

    private var _editState : MutableState<Boolean> = mutableStateOf(false)
    val editState  = _editState

    private val _updateCardsQueue : MutableState<MutableMap<Int,FlashCard>> = mutableStateOf(mutableMapOf());
    val updateCardsQueue = _updateCardsQueue

    private var _hasDeckModified : MutableState<Boolean> = mutableStateOf(false)
    val hasDeckModified  = _hasDeckModified

    fun onEvent(event : DeckEvents){
        when(event){
            is DeckEvents.OnHeaderChange -> {
                _headerTitle.value = event.value
            }
            is DeckEvents.OnCreateCard -> {
//                val cardList = _cardList.value.toMutableList()
//                cardList.add(FlashCard())
                _cardList.value.add(FlashCard())
                _deckToggle.value = true
            }
            is DeckEvents.OnCardRemove -> {
//                val cardList = _cardList.value.toMutableList()
//                cardList.removeAt(event.index)
                if (event.index >= 0 && _cardList.value.size >= 1){
                    _cardList.value.removeAt(event.index)
                }
            }
            is DeckEvents.OnDeckToggle -> {
                if (_cardList.value.isNotEmpty()){
                    _deckToggle.value = !_deckToggle.value
                }else{
                    _deckToggle.value = false
                }
            }
            is DeckEvents.OnEditStateChange -> {
                _editState.value = event.editState
            }
            is DeckEvents.OnUpdateCardChange -> {
                _cardList.value.set(event.index,event.card)
            }
            is DeckEvents.OnPersistCardChanges -> {

            }
        }
    }

    fun fetchDeckById(deckId : Long){
        _deckId.value = deckId
        viewModelScope.launch(Dispatchers.IO) {
            val deck = deckRepository.getDeckById(deckId)
            deck.also {
                _headerTitle.value = it.deckName
                _cardList.value.addAll(it.cards)
                _modifiedTime.value = it.modifiedDate.time
            }
        }
    }

    fun insertDeck() {
        if (_cardList.value.isNotEmpty()){
            _hasDeckModified.value = true
            viewModelScope.launch(Dispatchers.IO) {
                deckRepository.insertDeck(Deck(
                    deckName = if (_headerTitle.value.isEmpty()) getFormattedTitle() else _headerTitle.value,
                    totalCards = _cardList.value.size,
                    modifiedDate = Date(System.currentTimeMillis()),
                    cards = _cardList.value,
                    categoryId = 1L
                ))
                _hasDeckModified.value = false
            }
        }

    }

    private fun getFormattedTitle(): String {
        return "Untitled -${IDateGenerator.getGracefullyTimeFromEpoch(System.currentTimeMillis())}"
    }

    fun updateDeck() {
        if (_cardList.value.isNotEmpty()){
            _hasDeckModified.value = true
            val newDeck = Deck(
                deckId = _deckId.value,
                deckName = _headerTitle.value,
                totalCards = _cardList.value.size,
                modifiedDate = Date(System.currentTimeMillis()),
                cards = _cardList.value,
                categoryId = 2L
            )
            viewModelScope.launch(Dispatchers.IO) {
                deckRepository.updateDeck(
                    deckId = _deckId.value,
                    deck = newDeck
                )
                _hasDeckModified.value = false
            }
        }
    }

}