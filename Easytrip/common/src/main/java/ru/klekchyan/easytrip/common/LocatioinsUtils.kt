package ru.klekchyan.easytrip.common

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun getDeltaBetweenPoints(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
    val r = 6378.137
    val dLat = deg2rad(latitude2) - deg2rad(latitude1)
    val dLon = deg2rad(longitude2) - deg2rad(longitude1)
    val a = sin(dLat/2) * sin(dLat/2) +
            cos(deg2rad(latitude1)) * cos(deg2rad(latitude2)) *
            sin(dLon/2) * sin(dLon/2)
    val c = 2 * atan2(sqrt(a), sqrt(1-a))
    val d = r * c
    return d * 1000
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}