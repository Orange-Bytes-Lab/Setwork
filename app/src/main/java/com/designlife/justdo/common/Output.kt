package com.designlife.justdo.common

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
object AppOutput {

    // ───────────────────────────────────────────────
    // PDF EXPORT (FIXED: wrapping + multi-page)
    // ───────────────────────────────────────────────

    fun exportAsPdf(
        context: Context,
        fileName: String,
        content: String,
        openAfterSave: Boolean = true
    ) {

        if (content.isBlank()) {
            Toast.makeText(context, "Content is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842

        val marginX = 40f
        val marginY = 50f
        val usableWidth = pageWidth - (marginX * 2)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 14f
        }

        try {

            val staticLayout = buildStaticLayout(
                content,
                textPaint,
                usableWidth.toInt(),
                1.4f,
                8f
            )

            var currentLine = 0
            var pageNumber = 1

            while (currentLine < staticLayout.lineCount) {

                val page = pdfDocument.startPage(
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                )

                val canvas = page.canvas
                canvas.drawColor(Color.WHITE)

                var y = marginY
                val maxHeight = pageHeight - marginY

                while (currentLine < staticLayout.lineCount) {

                    val lineBottom = staticLayout.getLineBottom(currentLine)
                    if (lineBottom > maxHeight) break

                    val start = staticLayout.getLineStart(currentLine)
                    val end = staticLayout.getLineEnd(currentLine)

                    val lineText = content.substring(start, end)
                    canvas.drawText(lineText, marginX, y, textPaint)

                    y += textPaint.textSize + 8
                    currentLine++
                }

                pdfDocument.finishPage(page)
                pageNumber++
            }

            val uri = savePdf(context, fileName, pdfDocument)

            if (uri != null) {
                Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
                if (openAfterSave) openPdf(context, uri)
            } else {
                Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF generation failed", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    // ───────────────────────────────────────────────
    // PNG EXPORT (cleaned)
    // ───────────────────────────────────────────────

    fun exportAsPng(context: Context, fileName: String, content: String) {

        val width = 1080
        val paddingH = width * 0.07f
        val paddingV = width * 0.08f
        val usableWidth = width - (paddingH * 2)

        val bgColor = Color.parseColor("#FAFAF8")
        val textColor = Color.parseColor("#1C1C1E")
        val accentColor = Color.parseColor("#4A90D9")
        val subtleColor = Color.parseColor("#8E8E93")

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = width * 0.045f
            typeface = Typeface.create("serif", Typeface.BOLD)
        }

        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = width * 0.030f
        }

        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = subtleColor
            textSize = width * 0.022f
        }

        val titleLayout = buildStaticLayout(fileName, titlePaint, usableWidth.toInt(), 1.2f, 4f)
        val bodyLayout = buildStaticLayout(content, bodyPaint, usableWidth.toInt(), 1.6f, 6f)

        val dividerY = paddingV + titleLayout.height + paddingV * 0.25f
        val bodyY = dividerY + paddingV * 0.35f
        val totalHeight = (bodyY + bodyLayout.height + paddingV).toInt()

        val bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(bgColor)

        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            strokeWidth = width * 0.008f
        }

        canvas.drawLine(paddingH, 0f, paddingH + usableWidth, 0f, accentPaint)

        canvas.save()
        canvas.translate(paddingH, paddingV)
        titleLayout.draw(canvas)
        canvas.restore()

        canvas.drawLine(paddingH, dividerY, paddingH + usableWidth, dividerY, Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1.5f
        })

        canvas.save()
        canvas.translate(paddingH, bodyY)
        bodyLayout.draw(canvas)
        canvas.restore()

        val footer = "${content.length} chars · ${content.lines().size} lines"
        canvas.drawText(footer, paddingH, totalHeight - paddingV * 0.5f, footerPaint)

        saveImage(context, "$fileName.png", bitmap, "image/png")
        bitmap.recycle()
    }

    // ───────────────────────────────────────────────
    // STORAGE (FIXED — CORE ISSUE HERE)
    // ───────────────────────────────────────────────

    private fun savePdf(context: Context, fileName: String, pdf: PdfDocument): Uri? {
        return saveFile(
            context,
            "$fileName.pdf",
            "application/pdf"
        ) { output ->
            pdf.writeTo(output)
        }
    }

    private fun saveImage(context: Context, name: String, bitmap: Bitmap, mime: String) {
        saveFile(context, name, mime) { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }

    @Suppress("DEPRECATION")
    private fun saveFile(
        context: Context,
        fileName: String,
        mimeType: String,
        writer: (OutputStream) -> Unit
    ): Uri? {

        return try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val resolver = context.contentResolver

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                ) ?: return null

                resolver.openOutputStream(uri)?.use {
                    writer(it)
                } ?: return null

                // ✅ MOST IMPORTANT (visibility fix)
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)

                uri

            } else {

                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    fileName
                )

                FileOutputStream(file).use {
                    writer(it)
                }

                // ✅ REQUIRED for visibility (pre-29)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf(mimeType),
                    null
                )

                Uri.fromFile(file)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ───────────────────────────────────────────────
    // OPEN PDF
    // ───────────────────────────────────────────────

    fun openPdf(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    // ───────────────────────────────────────────────
    // TEXT LAYOUT
    // ───────────────────────────────────────────────

    @Suppress("DEPRECATION")
    private fun buildStaticLayout(
        text: String,
        paint: TextPaint,
        width: Int,
        spacingMult: Float,
        spacingAdd: Float
    ): StaticLayout {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(spacingAdd, spacingMult)
                .setIncludePad(false)
                .build()
        } else {
            StaticLayout(
                text,
                paint,
                width,
                Layout.Alignment.ALIGN_NORMAL,
                spacingMult,
                spacingAdd,
                false
            )
        }
    }
}