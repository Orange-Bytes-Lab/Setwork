package com.designlife.justdo.common.presentation.components

import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.ui.theme.PrimaryColor2
import com.designlife.justdo.ui.theme.contentStyle_One
import com.designlife.justdo.R
import com.designlife.justdo.ui.theme.PrimaryColor1


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetComponent(
    sheetState : ModalBottomSheetState,
    sheetLayoutVisible : Boolean,
    sheetCloseEvent : () -> Unit
) {


    ModalBottomSheetLayout(
        modifier = Modifier.clickable {
             sheetCloseEvent()
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetElevation = 12.dp,
        sheetContent = {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier
                    .fillMaxWidth(.5F)
                    .height(8.dp)
                    .background(color = PrimaryColor1)
                    .clickable {
                        sheetCloseEvent()
                    })
                Spacer(modifier = Modifier.height(20.dp))
                SheetItem(icon = R.drawable.ic_note, name = "Note") {

                }
                SheetItem(icon = R.drawable.ic_event, name = "Task") {

                }
                SheetItem(icon = R.drawable.ic_category, name = "Category") {

                }
            }

        },
    ) {
        if (sheetLayoutVisible){
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable {
                    sheetCloseEvent()
                }) {}
        }
    }
}

@Composable
fun SheetItem(
    @DrawableRes icon : Int,
    name: String,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Icon(painter = painterResource(id = icon), contentDescription = "Bottom Sheet Icon", tint = Color.Gray, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = "+ ${name}", style = contentStyle_One.copy(
            fontSize = 14.sp
        ))
    }
}