package ru.klekchyan.easytrip.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

const val keyRequestingLocationUpdates = "requesting_location_updates"

fun Context.requestingLocationUpdates(): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(keyRequestingLocationUpdates, false)
}

fun Context.setRequestingLocationUpdates(requestingLocationUpdates: Boolean) {
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(keyRequestingLocationUpdates, requestingLocationUpdates)
        .apply()
}

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