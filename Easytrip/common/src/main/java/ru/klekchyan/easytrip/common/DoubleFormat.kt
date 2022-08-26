package ru.klekchyan.easytrip.common

import android.content.Context
import java.text.DecimalFormat
import ru.klekchyan.easytrip.base_ui.R

fun Double.toHumanDistanceFormat(context: Context): String = when {
    this < 1000.0 -> this.toMetersFormat() + " " + context.resources.getString(R.string.meters)
    else -> (this / 1000.0).toKilometersFormat() + " " + context.resources.getString(R.string.kilometers)
}

private fun Double.toKilometersFormat() = DecimalFormat("#,###.##").format(this)

private fun Double.toMetersFormat() = DecimalFormat("#,###").format(this)