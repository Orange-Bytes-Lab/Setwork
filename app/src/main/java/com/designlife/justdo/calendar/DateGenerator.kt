package com.designlife.justdo.calendar

import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface DateGenerator {
    fun setupDates()
    fun getDateList() : StateFlow<List<Date>>
    suspend fun loadPreviousMonth()
    suspend fun loadNextMonth() : List<Date>
}