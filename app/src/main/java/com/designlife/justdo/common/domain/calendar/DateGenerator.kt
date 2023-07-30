package com.designlife.justdo.common.domain.calendar

import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface DateGenerator {
    fun setupDates()
    fun getDateList() : StateFlow<List<Date>>
    suspend fun loadPreviousMonth() : List<Date>
    suspend fun loadNextMonth() : List<Date>
}