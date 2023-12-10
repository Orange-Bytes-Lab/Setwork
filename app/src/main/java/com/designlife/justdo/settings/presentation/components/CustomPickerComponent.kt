package com.designlife.justdo.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.CustomGrey
import com.designlife.justdo.ui.theme.PickerItemStyle

@Composable
fun CustomPickerComponent(
    itemList : List<String>,
    onCloseClick : () -> Unit,
    onItemClick : (index : Int) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().clickable { onCloseClick() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7F)
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(12.dp)),
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.padding(top = 6.dp))
                itemList.forEachIndexed { index, title ->
                    if (index != 0){
                        Divider(color = Color.DarkGray, thickness = .2.dp)
                    }
                    Row(modifier = Modifier
                        .clickable {
                            onItemClick(index)
                            onCloseClick()
                        }
                        .padding(vertical = 10.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        ,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(text = title, style = PickerItemStyle)
                    }

                }
                Spacer(modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}