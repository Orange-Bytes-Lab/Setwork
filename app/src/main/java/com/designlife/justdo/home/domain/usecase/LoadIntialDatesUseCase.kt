package com.designlife.justdo.home.domain.usecase

import com.designlife.justdo.common.domain.calendar.DateGenerator

class LoadIntialDatesUseCase(
    private val dateGenerator: DateGenerator
) {

    suspend operator fun invoke(){
        dateGenerator.setupDates()
    }

}