package com.designlife.justdo.note.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.noteContentStyle
import com.designlife.justdo.ui.theme.noteContentStyleSize
import com.designlife.justdo.ui.theme.noteTitleStyle
import com.designlife.justdo.ui.theme.noteTitleStyleSize

@Composable
fun NoteComponent(
    title: String,
    onTitleUpdate: (newTitle: String) -> Unit,
    noteText: String,
    onNoteUpdate: (noteData: String) -> Unit,
) {
    val lineSpacing = 26.dp
    val lineSpacingPx = with(LocalDensity.current) { lineSpacing.toPx() }

    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalDivider(color = ButtonHighLightPrimary.value, thickness = 0.7.dp)
        BasicTextField(
            modifier = Modifier
                .padding(start = 5.dp, top = 2.dp, bottom = 2.dp)
                .fillMaxWidth(),
            value = title,
            singleLine = true,
            textStyle = noteTitleStyle.value.copy(
                color = TypographyColor.value,
                fontSize = noteTitleStyleSize.value,
            ),
            onValueChange = onTitleUpdate,
            cursorBrush = SolidColor(ButtonPrimary.value),
        ) { innerTextField ->
            if (title.isEmpty()) {
                Text(
                    text = "Untitled",
                    style = noteTitleStyle.value.copy(
                        color = Color.Gray,
                        fontSize = noteTitleStyleSize.value,
                    )
                )
            }
            innerTextField()
        }
        HorizontalDivider(color = ButtonHighLightPrimary.value, thickness = 0.7.dp)

        Box(modifier = Modifier.fillMaxSize()) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                val totalLines = (size.height / lineSpacingPx).toInt() + 1
                for (i in 1..totalLines) {
                    val y = i * lineSpacingPx
                    drawLine(
                        color = ButtonHighLightPrimary.value.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
            }

            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp),
                value = noteText,
                singleLine = false,
                textStyle = noteContentStyle.value.copy(
                    color = TypographyColor.value,
                    fontSize = noteContentStyleSize.value,
                    // ← key fix: lock line height to match ruled line spacing
                    lineHeight = with(LocalDensity.current) {
                        lineSpacingPx.toSp()
                    },
                ),
                onValueChange = onNoteUpdate,
                cursorBrush = SolidColor(ButtonPrimary.value),
            )
        }
    }
}