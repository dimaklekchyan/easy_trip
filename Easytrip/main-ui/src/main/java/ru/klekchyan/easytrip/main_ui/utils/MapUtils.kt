package ru.klekchyan.easytrip.main_ui.utils

import android.content.Context
import com.yandex.mapkit.geometry.Point
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

internal fun getDeltaBetweenPoints(point1: Point, point2: Point): Double =
    ru.klekchyan.easytrip.common.getDeltaBetweenPoints(
        latitude1 = point1.latitude,
        longitude1 = point1.longitude,
        latitude2 = point2.latitude,
        longitude2 = point2.longitude
    )

private fun Context.readRawResource(name: String): String {
    val builder = StringBuilder()
    val resourceIdentifier: Int = resources.getIdentifier(name, "raw", packageName)
    val inputStream: InputStream = resources.openRawResource(resourceIdentifier)
    val reader = BufferedReader(InputStreamReader(inputStream))
    try {
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }
    } catch (ex: IOException) {
        throw ex
    } finally {
        reader.close()
    }
    return builder.toString()
}

internal fun Context.getMapStyle(isDarkTheme: Boolean): String {
    //TODO Create appropriate styles
    return readRawResource(if(isDarkTheme) "customization_example" else "customization_example")
}