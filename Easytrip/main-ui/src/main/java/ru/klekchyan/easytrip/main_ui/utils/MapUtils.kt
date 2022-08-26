package ru.klekchyan.easytrip.main_ui.utils

import android.content.Context
import com.yandex.mapkit.geometry.Point
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun getDeltaBetweenPoints(point1: Point, point2: Point): Double {
    val r = 6378.137
    val dLat = deg2rad(point2.latitude) - deg2rad(point1.latitude)
    val dLon = deg2rad(point2.longitude) - deg2rad(point1.longitude)
    val a = sin(dLat/2) * sin(dLat/2) +
            cos(deg2rad(point1.latitude)) * cos(deg2rad(point2.latitude)) *
            sin(dLon/2) * sin(dLon/2)
    val c = 2 * atan2(sqrt(a), sqrt(1-a))
    val d = r * c
    return d * 1000
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

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