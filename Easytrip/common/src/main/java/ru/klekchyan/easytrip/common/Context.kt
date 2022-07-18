package ru.klekchyan.easytrip.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat

fun Context.checkLocationPermissions(
    onFineGranted: () -> Unit,
    onCoarseGranted: () -> Unit,
    onAllDenied: () -> Unit
) {
    val coarseIsGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val fineIsGranted = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    if(fineIsGranted == PackageManager.PERMISSION_GRANTED) {
        onFineGranted()
    } else if(coarseIsGranted == PackageManager.PERMISSION_GRANTED) {
        onCoarseGranted()
    } else {
        onAllDenied()
    }
}

fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}