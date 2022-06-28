package ru.klekchyan.easytrip.common

import android.content.Context
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