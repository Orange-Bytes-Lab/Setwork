package com.designlife.justdo.common.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.Purple500
import com.designlife.justdo.ui.theme.buttonStyle

@Composable
fun CustomButton(
    onButtonEvent : () -> Unit
) {
    Button(
        onClick = { onButtonEvent() },
        modifier = Modifier
            .width(60.dp)
            .height(32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ButtonPrimary,
            disabledBackgroundColor = Purple500
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(text = "Save", textAlign = TextAlign.Center, style = buttonStyle)
    }
}