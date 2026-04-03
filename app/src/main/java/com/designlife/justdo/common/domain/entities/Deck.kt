package com.designlife.justdo.common.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.designlife.justdo.common.data.entities.Deck
import com.designlife.justdo.common.utils.serializer.LongSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.String


data class Deck(
    val deckId : Long = 0L,
    val deckName : String = "",
    val totalCards : Int = 0,
    val modifiedDate : Date,
    val categoryId : Long,
    val cards : List<FlashCard> = emptyList()
){
    fun toDeck() : Deck{
        return com.designlife.justdo.common.data.entities.Deck(
            deckId = this.deckId,
            deckName = this.deckName,
            totalCards = this.totalCards,
            modifiedDate = this.modifiedDate.time,
            categoryId = this.categoryId,
            cards = this.cards.map { it.toFlashCard() }
        )
    }
}
