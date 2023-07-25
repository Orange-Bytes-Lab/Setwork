package com.designlife.justdo.home.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.calendar.IDateGenerator
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo.home.presentation.components.DateComponent
import com.designlife.justdo.home.presentation.components.HeaderComponent
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModel
import com.designlife.justdo.home.presentation.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private var initiallScroll = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dateGenerator = IDateGenerator()
        val loadDatesUseCase = LoadIntialDatesUseCase(dateGenerator)
        val loadNextDatesUseCase = LoadNextDatesSetUseCase(dateGenerator)
        val loadPreviousDatesUseCase = LoadPreviousDatesSetUseCase(dateGenerator)
        val factory = HomeViewModelFactory(dateGenerator,loadDatesUseCase,loadNextDatesUseCase,loadPreviousDatesUseCase)
        viewModel = ViewModelProvider(this,factory)[HomeViewModel::class.java]
        viewModel.loadInitialDates()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val listState = rememberLazyListState()
                val scope = rememberCoroutineScope()
                val currentDateIndex = viewModel.currentDateIndex.value
                LaunchedEffect(currentDateIndex){
                    if (!initiallScroll){
                        scrollToRollCurrentDate(currentDateIndex,listState,scope)
                        initiallScroll = true
                    }

                }
                val dateList = viewModel.dateList.value
                val currentMonth = viewModel.currentMonth.value
                val currentYear = viewModel.currentYear.value
                val currentDate = viewModel.currentDate.value
                val todayDateIndex = dateList.indexOf(currentDate)
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)) {
                    HeaderComponent(
                        headerText = "All Tasks",
                        onEventClick = {
                            scrollToRollCurrentDate(todayDateIndex,listState, scope)
                        },
                        currentDate = Date(System.currentTimeMillis()),
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    DateComponent(
                        listState = listState,
                        currentDate = currentDate,
                        currentMonth = currentMonth,
                        currentYear = currentYear,
                        dateList = dateList,
                        onEventClick = {
                             viewModel.fetchDateDataByDate(it)
                        },
                        onChangeVisibleDate = {
                            viewModel.onYearChange(it)
                            viewModel.onMonthChange(it)
                        },
                        loadPreviousTrigger = {
                            scrollToRollCurrentDate(currentDateIndex,listState,scope)
                            viewModel.loadPreviousMonth()
                        },
                        loadNextTrigger = {
                            viewModel.loadNextMonth()
//                            scope.launch(Dispatchers.Main) {
//                                scrollToRollCurrentDate(currentDateIndex,listState)
//                            }
                        },
                    )
                }
            }
        }
    }



    private fun scrollToRollCurrentDate(currentDateIndex: Int,listState : LazyListState,scope : CoroutineScope) {
        scope.launch(Dispatchers.Main) {
            listState.animateScrollToItem(currentDateIndex)
        }
    }
}

