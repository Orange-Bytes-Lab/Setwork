package com.designlife.justdo.note.presentation.components

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.designlife.justdo.note.ListAutoFormatWatcher
import com.designlife.justdo.ui.theme.ButtonHighLightPrimary
import com.designlife.justdo.ui.theme.ButtonPrimary
import com.designlife.justdo.ui.theme.TypographyColor
import com.designlife.justdo.ui.theme.noteContentStyleSize
import com.designlife.justdo.ui.theme.noteTitleStyle
import com.designlife.justdo.ui.theme.noteTitleStyleSize

@Composable
fun NoteComponent(
    title: String,
    onTitleUpdate: (String) -> Unit,
    noteText: String,
    onNoteUpdate: (String) -> Unit,
) {
    val lineColor = ButtonHighLightPrimary.value.copy(alpha = 0.5f)
    val textColor = TypographyColor.value
    val cursorColor = ButtonPrimary.value
    val contentSizeSp = noteContentStyleSize.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        HorizontalDivider(color = ButtonHighLightPrimary.value, thickness = 0.7.dp)
        TitleField(
            title = title,
            onTitleUpdate = onTitleUpdate,
            textColor = textColor,
            cursorColor = cursorColor
        )
        HorizontalDivider(color = ButtonHighLightPrimary.value, thickness = 0.7.dp)
        NoteEditor(
            noteText = noteText,
            onNoteUpdate = onNoteUpdate,
            textColor = textColor,
            cursorColor = cursorColor,
            contentSizeSp = contentSizeSp,
            lineColor = lineColor
        )
    }
}


@Composable
private fun TitleField(
    title: String,
    onTitleUpdate: (String) -> Unit,
    textColor: Color,
    cursorColor: Color
) {
    BasicTextField(
        value = title,
        onValueChange = onTitleUpdate,
        singleLine = true,
        textStyle = noteTitleStyle.value.copy(
            color = textColor,
            fontSize = noteTitleStyleSize.value
        ),
        cursorBrush = SolidColor(cursorColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        decorationBox = { inner ->
            if (title.isEmpty()) {
                Text(
                    "Untitled",
                    color = Color.Gray,
                    fontSize = noteTitleStyleSize.value
                )
            }
            inner()
        }
    )
}

@Composable
private fun NoteEditor(
    noteText: String,
    onNoteUpdate: (String) -> Unit,
    textColor: Color,
    cursorColor: Color,
    contentSizeSp: TextUnit,
    lineColor: Color
) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection()),
        factory = { ctx ->
            object : androidx.appcompat.widget.AppCompatEditText(ctx) {
                private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = lineColor.toArgb()
                    strokeWidth = 0.5f * resources.displayMetrics.density
                    style = Paint.Style.STROKE
                }

                override fun onDraw(canvas: Canvas) {
                    val lineCount = layout?.lineCount ?: 0
                    for (i in 0 until lineCount) {
                        val baseline = layout.getLineBaseline(i).toFloat() + totalPaddingTop
                        canvas.drawLine(0f, baseline, width.toFloat(), baseline, linePaint)
                    }
                    if (lineCount > 0) {
                        val lineHeight = layout.getLineBottom(0) - layout.getLineTop(0).toFloat()
                        val lastBaseline =
                            layout.getLineBaseline(lineCount - 1).toFloat() + totalPaddingTop
                        var y = lastBaseline + lineHeight
                        while (y < height) {
                            canvas.drawLine(0f, y, width.toFloat(), y, linePaint)
                            y += lineHeight
                        }
                    }
                    super.onDraw(canvas)
                }
            }.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                background = null
                setTextColor(textColor.toArgb())
                setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSizeSp.value)
                setPadding(
                    16.dp.toPxInt(),
                    12.dp.toPxInt(),
                    16.dp.toPxInt(),
                    60.dp.toPxInt()
                )
                setCursorColor(cursorColor.toArgb())
                inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                gravity = Gravity.TOP or Gravity.START
                setHorizontallyScrolling(false)
                maxLines = Int.MAX_VALUE
                movementMethod = ScrollingMovementMethod()
                overScrollMode = View.OVER_SCROLL_NEVER
                // Use exact spacing — no multiplier drift
                setLineSpacing(0f, 1f)
                setText(noteText)
                setSelection(noteText.length)
                addTextChangedListener(
                    ListAutoFormatWatcher { updated ->
                        onNoteUpdate(updated)
                    }
                )
            }
        },
        update = { editText ->
            val current = editText.text.toString()
            if (current != noteText) {
                val cursor = editText.selectionStart.coerceAtMost(noteText.length)
                editText.setText(noteText)
                editText.setSelection(cursor)
            }
            editText.setTextColor(textColor.toArgb())
        }
    )
}

private fun Dp.toPxInt(): Int =
    (this.value * Resources.getSystem().displayMetrics.density).toInt()

@SuppressLint("DiscouragedPrivateApi")
fun EditText.setCursorColor(@ColorInt color: Int) {
    try {
        val drawable = GradientDrawable().apply {
            setColor(color)
            setSize(2, this@setCursorColor.lineHeight)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textCursorDrawable = drawable
        } else {
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editor = editorField.get(this)

            val cursorDrawableField = editor.javaClass.getDeclaredField("mCursorDrawable")
            cursorDrawableField.isAccessible = true
            cursorDrawableField.set(editor, arrayOf(drawable, drawable))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}