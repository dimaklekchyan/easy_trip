package ru.klekchyan.easytrip.main_ui.utils

internal fun List<String>.toRequestFormat(): String? {
    return if(this.isNotEmpty()) {
        this.joinToString(separator = ",")
    } else {
        null
    }
}