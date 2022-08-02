package ru.klekchyan.easytrip.common

import java.text.DecimalFormat

fun Double.toHumanDistanceFormat(): String = when {
    this < 1000.0 -> this.toMetersFormat() + " m."
    else -> (this / 1000).toKilometersFormat() + " km."
}

fun Double.toKilometersFormat() = DecimalFormat("#,###.##").format(this).replace(',', ' ')
    .replace('.', ',')

private fun Double.toMetersFormat() = DecimalFormat("#,###").format(this).replace(',', ' ')