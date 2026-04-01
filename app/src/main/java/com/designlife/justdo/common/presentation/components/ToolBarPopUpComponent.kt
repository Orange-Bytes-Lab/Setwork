package com.designlife.justdo.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.designlife.justdo.R
import com.designlife.justdo.ui.theme.ComponentBackground
import com.designlife.justdo.ui.theme.LightButtonPrimary
import com.designlife.justdo.ui.theme.NotePopUpItemStyle
import com.designlife.justdo.ui.theme.PrimaryBackgroundColor
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.UIComponentBackground

@Composable
fun ToolBarPopUpComponent(
    onExportPdfEvent : () -> Unit,
    onExportPngEvent : () -> Unit,
    onDeleteEvent : () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(top = 72.dp)
            .height(180.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ComponentBackground.value, shape = RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PopItem("Export pdf", R.drawable.ic_export_pdf) { onExportPdfEvent() }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(.96F),
                thickness = 2.dp,
                color = LightButtonPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Export png", R.drawable.ic_export_png) { onExportPngEvent() }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(.96F),
                thickness = 2.dp,
                color = LightButtonPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            PopItem("Delete", R.drawable.ic_delete) { onDeleteEvent() }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun PopItem(
    itemTitle: String,
    @DrawableRes iconRes: Int,
    onItemEvent: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(30.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onItemEvent()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = itemTitle, style = NotePopUpItemStyle.value.copy(color = TypographyColor.value), textAlign = TextAlign.Start)
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