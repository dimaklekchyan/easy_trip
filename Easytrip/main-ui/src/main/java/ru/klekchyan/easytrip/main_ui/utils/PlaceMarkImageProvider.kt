package ru.klekchyan.easytrip.main_ui.utils

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.toArgb
import com.yandex.runtime.image.ImageProvider
import ru.klekchyan.easytrip.base_ui.theme.colorGray
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.base_ui.R
import java.lang.Integer.max
import kotlin.math.abs

class PlaceMarkImageProvider(
    private val context: Context,
    private val place: SimplePlace,
    private val density: Float,
    private val isDarkTheme: Boolean,
    private val isClicked: Boolean,
): ImageProvider() {

    private val text: String = place.name.ifEmpty { context.resources.getString(R.string.no_name) }

    override fun getId(): String = place.xid + isDarkTheme.toString() + isClicked.toString()

    override fun getImage(): Bitmap {
        val textPaint = Paint().apply {
            textSize = FONT_SIZE * density
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            isAntiAlias = true
            color = if(isDarkTheme) {
                if(isClicked) Color.BLACK else Color.WHITE
            } else {
                if(isClicked) Color.WHITE else Color.BLACK
            }
        }

        val textWidth = textPaint.measureText(text)
        val textMetrics = textPaint.fontMetrics
        val textHeight = abs(textMetrics.bottom) + abs(textMetrics.top)
        val placeMarkWidth = (2 * PLACE_MARK_RADIUS * density).toInt()
        val textBackgroundWidth = (textWidth + placeMarkWidth / 2 + MARGIN_SIZE * 2 * density).toInt()
        val textBackgroundHeight = (textHeight + MARGIN_SIZE * 2 * density).toInt()
        val bitmap = Bitmap.createBitmap(
            placeMarkWidth + textBackgroundWidth,
            max(placeMarkWidth, textBackgroundHeight),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        val placeMarkPaint = Paint().apply {
            color = place.color.toArgb()
        }
        val backgroundTextPaint = Paint().apply {
            isAntiAlias = true
            color = if(isDarkTheme) {
                if(isClicked) Color.WHITE else colorGray.toArgb()
            } else {
                if(isClicked) colorGray.toArgb() else Color.WHITE
            }
            setShadowLayer(10f, 1f, 1f, Color.LTGRAY)
        }

        val textBackgroundLeft = (placeMarkWidth / 2).toFloat() - 20f
        val textBackgroundTop = (placeMarkWidth / 2).toFloat() - (textBackgroundHeight / 2).toFloat()
        val textBackgroundRight = textBackgroundLeft + textBackgroundWidth.toFloat()
        val textBackgroundBottom = (placeMarkWidth / 2).toFloat() + (textBackgroundHeight / 2).toFloat()

        canvas.drawRoundRect(
            RectF(textBackgroundLeft, textBackgroundTop, textBackgroundRight, textBackgroundBottom),
            50f,
            50f,
            backgroundTextPaint
        )
        canvas.drawCircle(
            (placeMarkWidth / 2).toFloat(),
            (placeMarkWidth / 2).toFloat(),
            (placeMarkWidth / 2).toFloat(),
            placeMarkPaint
        )

        val textLeft = (textBackgroundLeft + (textBackgroundRight - textBackgroundLeft) / 2) + placeMarkWidth / 4
        val textTop = (textBackgroundTop + (textBackgroundBottom - textBackgroundTop) / 2) + textHeight / 4
        canvas.drawText(
            text,
            textLeft,
            textTop,
            textPaint
        )
        return bitmap
    }

    companion object {
        private const val FONT_SIZE = 11.5f
        private const val MARGIN_SIZE = 10f
        private const val PLACE_MARK_RADIUS = 20F

        fun getInstance(
            context: Context,
            place: SimplePlace,
            density: Float,
            isDarkTheme: Boolean,
            isClicked: Boolean
        ): PlaceMarkImageProvider {
            return PlaceMarkImageProvider(context, place, density, isDarkTheme, isClicked)
        }
    }
}