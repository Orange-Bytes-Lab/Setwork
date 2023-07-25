package com.designlife.justdo.home.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.calendar.IDateGenerator
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.fontFamily
import com.designlife.justdo.ui.theme.headerStyle
import java.util.Date


@Composable
fun DateComponent(
    listState : LazyListState,
    currentDate : Date,
    currentMonth : String,
    currentYear : String,
    dateList : List<Date>,
    onEventClick : (index : Int) -> Unit,
    onChangeVisibleDate : (date : Date) -> Unit,
    loadPreviousTrigger : () -> Unit,
    loadNextTrigger : () -> Unit,
) {

    val firstVisibleIndex = remember {
        mutableStateOf(listState.firstVisibleItemIndex)
    }

    val dateListSize = remember {
        mutableStateOf(dateList.size)
    }

    LaunchedEffect(dateList){
        dateListSize.value = dateList.size
    }

    LaunchedEffect(listState){
        snapshotFlow {
            listState.firstVisibleItemIndex
        }.collect{index ->
            if (index == 1){
                loadPreviousTrigger()

            }

        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = buildAnnotatedString {

                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = FontWeight.Light)){
                    append("CALENDAR ")
                }


                withStyle(style = SpanStyle(color = Color.Black, fontSize = 12.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold)){
                    append("$currentMonth ")
                }

                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp, fontFamily = fontFamily, fontWeight = FontWeight.Bold)){
                    append(currentYear)
                }

            })
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (dateListSize.value != 0){
            LazyRow(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(horizontal = 12.dp),
                state = listState
            ){
                items(
                    count = dateList.size,
                    key = {
                        dateList[it].time
                    }
                ){ index ->

                    DateItem(isCurrent = dateList[index].time.equals(currentDate.time), date = dateList[index]) {
                        onEventClick(index)
                    }

                    if (index == dateList.size-1){
                        DisposableEffect(Unit){
                            loadNextTrigger()

                            onDispose {  }
                        }
                    }
                    onChangeVisibleDate(dateList[index])
                    Log.i("CHECK_TIME", "DateComponent: ${dateList[index].time} == ${currentDate.time}")

                }
            }
        }

    }

}

@Composable
fun DateItem(
    isCurrent : Boolean,
    date : Date,
    onEventClick : () -> Unit,
) {

    val pair = IDateGenerator.getDayInfoFrom(date)
    Column(modifier = Modifier
        .padding(horizontal = 8.dp)
        .width(60.dp)
        .height(76.dp)
        .background(
            color = if (isCurrent) ButtonPrimary else Color.White,
            shape = RoundedCornerShape(12)
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "${pair.first}",
            style = headerStyle.copy(
                color = if (isCurrent) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pair.second,
            style = headerStyle.copy(
                color = if (isCurrent) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )

    }
}

@Preview
@Composable
fun DateComponentPreview() {

//    val dateGenerator = IDateGenerator()
//
//    var list = listOf<Date>()
//    LaunchedEffect(Unit){
//        CoroutineScope(Dispatchers.IO).launch {
//            dateGenerator.getDateList().collect{
//                list = it
//            }
//        }
//    }
//
//    val currentMonth = remember {
//        mutableStateOf(IDateGenerator.getMonthFromDate(dateGenerator.getToday()))
//    }
//    Column(
//        modifier = Modifier.fillMaxWidth().background(color = Color.Gray),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        DateComponent(
//            listState = rememberLazyListState(),
//            currentDate = dateGenerator.getToday(),
//            currentMonth = currentMonth.value,
//            dateList = list,
//            onEventClick = {index -> },
//            onChangeVisibleMonth = { month ->
//                currentMonth.value = month
//            }
//        )
//    }

}