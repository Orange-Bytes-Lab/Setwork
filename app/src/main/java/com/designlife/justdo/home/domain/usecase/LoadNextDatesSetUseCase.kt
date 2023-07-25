package com.designlife.justdo.home.domain.usecase

import com.designlife.justdo.calendar.DateGenerator
import java.util.Date

class LoadNextDatesSetUseCase(
    private val dateGenerator: DateGenerator
) {

    suspend operator fun invoke() : List<Date>{
        return dateGenerator.loadNextMonth()
    }

}