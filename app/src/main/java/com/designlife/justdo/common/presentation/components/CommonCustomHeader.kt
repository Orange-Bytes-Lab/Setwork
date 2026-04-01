package com.designlife.justdo.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.designlife.justdo.R
import com.designlife.justdo.common.utils.enums.ViewType
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.IconColor
import com.designlife.justdo.ui.theme.NotePopUpItemStyle
import com.designlife.justdo.ui.theme.Shapes
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.cutBottomRoundedCorners
import com.designlife.justdo.ui.theme.headerStyle
import kotlinx.coroutines.delay

@Composable
fun CommonCustomHeader(
    headerTitle: String,
    hasDone: Boolean = false,
    forTask: Boolean = false,
    isOverview: Boolean = false,
    viewType: ViewType = ViewType.UNKNOWN,
    onCloseEvent: () -> Unit,
    onAutoSaveEvent: () -> Unit = {},
    onReminderEvent: () -> Unit = {},
    onAIChatEvent: () -> Unit = {},
    onThreeDotEvent: () -> Unit = {},
    onButtonClickEvent: () -> Unit
) {

    LaunchedEffect(viewType == ViewType.NOTE) {
        if (viewType == ViewType.NOTE) {
            delay(15000)
            onAutoSaveEvent()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp, max = 72.dp)
            .clip(Shapes.cutBottomRoundedCorners(16.dp))
            .background(ComponentBackground.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    onCloseEvent()
                }) {
                    Column(
                        modifier = Modifier
                            .size(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                            tint = IconColor.value
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (headerTitle.length > 14) "${
                        headerTitle.substring(
                            0,
                            13
                        )
                    } .." else headerTitle,
                    style = headerStyle.value.copy(color = TypographyColor.value)
                )

            }
            if (viewType == ViewType.TASK) {
                if (forTask) {
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomButton(
                            buttonText = if (isOverview || hasDone) "Delete" else "Save",
                            isDangerButton = isOverview
                        ) {
                            onButtonClickEvent()
                        }
                    }
                }
            }
            if (viewType == ViewType.NOTE) {
                Row(
                    modifier = Modifier
                        .padding(end = 12.dp) // check
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        onIconEvent = { onReminderEvent() },
                        iconRes = R.drawable.ic_reminder
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.wrapContentSize().rotate(rotation)
                    ) {
                        CustomIconButton(
                            onIconEvent = { /* auto launching from launched effects */ },
                            iconRes = R.drawable.ic_auto_save
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    CustomIconButton(
                        onIconEvent = { onAIChatEvent() },
                        iconRes = R.drawable.ic_ai_chat
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    CustomIconButton(
                        onIconEvent = { onThreeDotEvent() },
                        iconRes = R.drawable.ic_three_dot
                    )
                }
            }
        }
    }
}

@Composable
fun CustomIconButton(
    onIconEvent: () -> Unit,
    @DrawableRes iconRes: Int
) {
    IconButton(onClick = { onIconEvent() }) {
        Column(
            modifier = Modifier
                .size(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = painterResource(iconRes),
                contentDescription = "Tooltip Icon"
            )
        }
    }
}