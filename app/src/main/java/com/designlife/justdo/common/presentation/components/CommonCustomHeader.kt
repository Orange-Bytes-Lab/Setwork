package com.designlife.justdo.common.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.IconColor
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.headerStyle

@Composable
fun CommonCustomHeader(
    headerTitle : String,
    hasDone : Boolean = false,
    forTask : Boolean = false,
    isOverview : Boolean = false,
    onCloseEvent : () -> Unit,
    onButtonClickEvent : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(Shapes.cutBottomRoundedCorners(15.dp))
            .background(ComponentBackground.value),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.wrapContentWidth()) {
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onCloseEvent() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon", tint = IconColor.value)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = headerTitle,
                    style = headerStyle.value.copy(color = TypographyColor.value)
                )
                Log.i("UPDATE", "CommonCustomHeader: Header Font Size ${headerStyle.value.fontSize}")
            }
            if (forTask){
                Row(modifier = Modifier
                    .padding(end = 10.dp)
                    .wrapContentWidth()) {
                    CustomButton(buttonText = if (isOverview || hasDone) "Delete" else "Save", isDangerButton = isOverview) {
                        onButtonClickEvent()
                    }
                }
            }
        }
    }
    
}