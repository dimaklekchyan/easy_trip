package ru.klekchyan.easytrip.main_ui.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import com.yandex.runtime.image.ImageProvider

class ClusterImageProvider private constructor(
    private val size: Int,
    private val density: Float
) : ImageProvider() {

    private val backgroundColor: Int = when {
        size > 100 -> colorMoreThan100
        size > 50 -> colorMoreThan50
        size > 20 -> colorMoreThan20
        else -> colorMoreThan10
    }

    override fun getId() = "text_$size"

    override fun getImage(): Bitmap {
        val textPaint = Paint()
        textPaint.textSize = FONT_SIZE * density
        textPaint.textAlign = Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        textPaint.color = Color.WHITE
        val widthF = textPaint.measureText("$size")
        val textMetrics = textPaint.fontMetrics
        val heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top)
        val textRadius = Math.sqrt((widthF * widthF + heightF * heightF).toDouble())
            .toFloat() / 2
        val internalRadius: Float = textRadius + MARGIN_SIZE * density
        val externalRadius: Float = internalRadius + STROKE_SIZE * density
        val width = (2 * externalRadius + 0.5).toInt()
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val backgroundPaint = Paint()
        backgroundPaint.isAntiAlias = true
        backgroundPaint.color = backgroundColor
        canvas.drawCircle(
            (width / 2).toFloat(),
            (width / 2).toFloat(),
            externalRadius,
            backgroundPaint
        )
        backgroundPaint.color = backgroundColor
        canvas.drawCircle(
            (width / 2).toFloat(),
            (width / 2).toFloat(),
            internalRadius,
            backgroundPaint
        )
        canvas.drawText(
            "$size", (width / 2).toFloat(), width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
            textPaint
        )
        return bitmap
    }

    companion object {
        private const val FONT_SIZE = 15f
        private const val MARGIN_SIZE = 3f
        private const val STROKE_SIZE = 3f

        private const val colorMoreThan100 = 0xFF283593.toInt()
        private const val colorMoreThan50 = 0xFF3949ab.toInt()
        private const val colorMoreThan20 = 0xFF5c6bc0.toInt()
        private const val colorMoreThan10 = 0xFF9fa8da.toInt()

        fun getInstance(size: Int, density: Float) = ClusterImageProvider(size, density)
    }
}