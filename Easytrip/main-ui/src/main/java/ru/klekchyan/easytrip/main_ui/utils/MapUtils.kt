package ru.klekchyan.easytrip.main_ui.utils

import com.yandex.mapkit.geometry.Point

internal fun getDeltaBetweenPoints(point1: Point, point2: Point): Double {
    val theta = point1.longitude - point2.longitude
    var dist = (Math.sin(deg2rad(point1.latitude))
            * Math.sin(deg2rad(point2.latitude))
            + (Math.cos(deg2rad(point1.latitude))
            * Math.cos(deg2rad(point2.latitude))
            * Math.cos(deg2rad(theta))))
    dist = Math.acos(dist)
    dist = rad2deg(dist)
    dist = dist * 60 * 1.1515
    return dist
}

private fun deg2rad(deg: Double): Double {
    return deg * Math.PI / 180.0
}

private fun rad2deg(rad: Double): Double {
    return rad * 180.0 / Math.PI
}