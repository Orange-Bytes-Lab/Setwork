package com.designlife.justdo.home.domain.usecase

import com.designlife.justdo.calendar.DateGenerator

class LoadPreviousDatesSetUseCase(
    private val dateGenerator: DateGenerator
) {

    suspend operator fun invoke(){
        dateGenerator.loadPreviousMonth()
    }

}