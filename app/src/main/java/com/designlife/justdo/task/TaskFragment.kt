package com.designlife.justdo.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.designlife.justdo.R
import com.designlife.justdo.common.presentation.components.CommonCustomHeader
import com.designlife.justdo.task.presentation.components.TaskItemDate
import com.designlife.justdo.task.presentation.components.TaskItemView
import com.designlife.justdo.task.presentation.events.TaskEvents
import com.designlife.justdo.task.presentation.viewmodel.TaskViewModel
import com.designlife.justdo.task.presentation.viewmodel.TaskViewModelFactory
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import java.util.Calendar

class TaskFragment : Fragment() {

    private lateinit var viewmodel : TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = TaskViewModelFactory()
        viewmodel = ViewModelProvider(this,factory)[TaskViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val inputText = viewmodel.titleValue.value
                val noteText = viewmodel.noteValue.value
                val selectedDateText = viewmodel.selectedDateText.value
                val selectedTimeText = viewmodel.selectedTimeText.value
                val calendar = Calendar.getInstance()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = PrimaryBackgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonCustomHeader(headerTitle = "New Task", onCloseEvent = { findNavController().navigateUp()}) {
                        Log.i("GEN_DATE_TIME", "onCreateView: ${viewmodel._rawTaskDateTimeInstance.time}")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ){
                        item{
                            TaskItemView(
                                hasIcon = false,
                                color = ButtonPrimary,
                                icon = R.drawable.ic_schedule,
                                labelText = "Add Title",
                                isNote = false,
                                inputText = inputText,
                                onInputChange = {
                                    viewmodel.onEvent(TaskEvents.OnTitleChange(it))
                                }
                            )
                        }
                        item{
                            Spacer(modifier = Modifier.height(14.dp))
                            TaskItemView(
                                hasIcon = true,
                                color = ButtonPrimary,
                                icon = R.drawable.ic_note,
                                labelText = "Note",
                                isNote = true,
                                inputText = noteText,
                                onInputChange = {
                                    viewmodel.onEvent(TaskEvents.OnNoteChange(it))
                                }
                            )
                        }
                        item{
                            Spacer(modifier = Modifier.height(14.dp))
                            TaskItemDate(
                                dateText = selectedDateText,
                                timeText = selectedTimeText,
                                icon = R.drawable.ic_schedule,
                                labelText = "Date",
                                onDateChange = {

                                    val year = calendar[Calendar.YEAR]
                                    val month = calendar[Calendar.MONTH]
                                    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
                                    val datePicker = DatePickerDialog(
                                        context,
                                        R.style.CustomDatePickerTheme,
                                        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                                            viewmodel.onEvent(TaskEvents.OnDateChange("$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"))
                                        }, year, month, dayOfMonth
                                    )
                                    datePicker.show()
                                },
                                onTimeChange = {
                                    val hour = calendar[Calendar.HOUR_OF_DAY]
                                    val minute = calendar[Calendar.MINUTE]

                                    val timePicker = TimePickerDialog(
                                        context,
                                        R.style.CustomTimePickerTheme,
                                        { _, selectedHour: Int, selectedMinute: Int ->
                                            viewmodel.onEvent(TaskEvents.OnTimeChange("$selectedHour:$selectedMinute"))
                                        }, hour, minute, false
                                    )
                                    timePicker.show()
                                }
                            )
                        }
                        item{
                            Spacer(modifier = Modifier.height(14.dp))
                            TaskItemView(
                                hasIcon = true,
                                color = ButtonPrimary,
                                icon = R.drawable.ic_repeat,
                                labelText = "Repeat",
                                isNote = false,
                                inputText = "Repeat Text",
                                onInputChange = {},
                                isClickable = true
                            ){

                            }
                        }
                        item{
                            Spacer(modifier = Modifier.height(14.dp))
                            TaskItemView(
                                hasIcon = true,
                                color = ButtonPrimary,
                                icon = R.drawable.ic_category,
                                labelText = "Category",
                                isNote = false,
                                inputText = "Category Text",
                                onInputChange = {},
                                isClickable = true
                            ){

                            }
                        }
                    }
                }
            }
        }
    }
}