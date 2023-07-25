package com.designlife.justdo.home.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.justdo.calendar.DateGenerator
import com.designlife.justdo.calendar.IDateGenerator
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class HomeViewModel(
    private val dateGenerator: DateGenerator,
    private val loadInitialDateUseCase : LoadIntialDatesUseCase,
    private val loadNextDatesSetUseCase: LoadNextDatesSetUseCase,
    private val loadPreviousDatesSetUseCase: LoadPreviousDatesSetUseCase
) : ViewModel() {

    private val _dateList : MutableState<List<Date>> = mutableStateOf(listOf());
    val dateList = _dateList

    private val _currentMonth : MutableState<String> = mutableStateOf(IDateGenerator.getMonthFromDate(Date(System.currentTimeMillis())))
    val currentMonth = _currentMonth

    private val _currentYear : MutableState<String> = mutableStateOf(IDateGenerator.getCurrentYear().toString())
    val currentYear = _currentYear

    private val _currentDate : MutableState<Date> = mutableStateOf(IDateGenerator.getToday())
    val currentDate = _currentDate

    private var _currentDateIndex : MutableState<Int> = mutableStateOf(-1)
    val currentDateIndex  = _currentDateIndex

    private var previousIndex : Int = _currentDateIndex.value

    init {
        viewModelScope.launch(Dispatchers.Default) {
            dateGenerator.getDateList().collect{
                if (_currentDateIndex.value == -1) {
                    _currentDateIndex.value = it.indexOf(_currentDate.value)
                    previousIndex = _currentDateIndex.value
                }
                _dateList.value = it
            }
        }
    }


    public fun fetchDateDataByDate(index : Int){
        // call use case and perform operation
    }

    fun onMonthChange(date: Date) {
        _currentMonth.value = IDateGenerator.getMonthFromDate(date)
    }

    fun onYearChange(date: Date) {
        _currentYear.value = IDateGenerator.getYearFromDate(date)
    }


    fun loadInitialDates(){
        viewModelScope.launch(Dispatchers.Default) {
            loadInitialDateUseCase()
        }
    }

    fun loadNextMonth(){
        viewModelScope.launch(Dispatchers.Default) {
            val list = mutableListOf<Date>()
            list.addAll(_dateList.value)
            list.addAll(loadNextDatesSetUseCase())
            _dateList.value = list
        }
    }

    fun loadPreviousMonth(){
        viewModelScope.launch(Dispatchers.Default) {
            loadPreviousDatesSetUseCase()
            withContext(Dispatchers.Main){
                _currentDateIndex.value = IDateGenerator.prevIndex
            }
        }
    }


}