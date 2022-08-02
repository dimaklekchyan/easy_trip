package ru.klekchyan.easytrip.main_ui.utils

import com.yandex.mapkit.geometry.Point
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