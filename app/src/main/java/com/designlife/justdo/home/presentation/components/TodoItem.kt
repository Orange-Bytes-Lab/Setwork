package com.designlife.justdo.home.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.designlife.justdo.common.domain.entities.Todo
import com.designlife.justdo.ui.theme.PrimaryColor1
import com.designlife.justdo.ui.theme.taskItemStyle

@Composable
fun TodoItem(
    color : Color,
    todo : Todo,
    onTodoEvent : () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clickable {
                onTodoEvent()
            }
            .height(50.dp),
        shape = RoundedCornerShape(40),
        backgroundColor = PrimaryColor1,
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PrimaryColor1, shape = RoundedCornerShape(20)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(20.dp)
                    .clip(RoundedCornerShape(100))
                    .background(color = if (todo.isCompleted) color else Color.Transparent)
                    .border(width = 2.dp, color = color, shape = RoundedCornerShape(100)),
                contentAlignment = Alignment.Center
            ) {
                if (todo.isCompleted){
                    Icon(modifier = Modifier
                        .size(15.dp)
                        .clip(RoundedCornerShape(100)),imageVector = Icons.Default.Check, contentDescription = "Done", tint = Color.White)
                }
            }
            Text(
                text = if (todo.title.length > 40) "${todo.title.substring(0,38)} ..." else todo.title,
                style = taskItemStyle.copy(
                    fontSize = 14.sp
                ),
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
        }
    }
}