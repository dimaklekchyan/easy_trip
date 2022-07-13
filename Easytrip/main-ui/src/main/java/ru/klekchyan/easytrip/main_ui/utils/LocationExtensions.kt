package ru.klekchyan.easytrip.main_ui.utils

import com.yandex.mapkit.geometry.Point
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation

internal fun CurrentUserLocation.toPoint() = Point(latitude, longitude)