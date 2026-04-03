package com.designlife.justdo.common.domain.entities

import com.designlife.justdo.common.data.entities.FlashCard

data class FlashCard(
    val frontContent : String = "",
    val backContent : String = ""
){
    fun toFlashCard() : FlashCard {
        return FlashCard(
            frontContent = this.frontContent,
            backContent = this.backContent
        )
    }
}
