package ru.klekchyan.easytrip.main_ui.utils

import android.graphics.Bitmap
import com.yandex.runtime.image.ImageProvider
import ru.klekchyan.easytrip.domain.entities.SimplePlace

class PlaceMarkImageProvider(
    private val place: SimplePlace
): ImageProvider() {
    override fun getId(): String = place.xid

    override fun getImage(): Bitmap {
        TODO("Not yet implemented")
    }
}