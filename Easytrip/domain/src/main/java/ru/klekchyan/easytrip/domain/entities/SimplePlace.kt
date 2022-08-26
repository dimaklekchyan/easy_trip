package ru.klekchyan.easytrip.domain.entities

import androidx.compose.runtime.Immutable
import ru.klekchyan.easytrip.base_ui.theme.*
import ru.klekchyan.easytrip.common.R

@Immutable
data class SimplePlace(
    val xid: String,
    val name: String,
    //Comma-separated list of categories
    val kinds: List<String>,
    //OpenStreetMap identifier of the object
    val osm: String? = null,
    //Wikidata identifier of the object
    val wikidata: String? = null,
    //Distance in meters from selected point (for radius query)
    val dist: Double? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
) {
    val icon = when {
        kinds.contains("tourist_facilities") -> R.drawable.ic_tourist_facility_point
        kinds.contains("sport") -> R.drawable.ic_sport_point
        kinds.contains("interesting_places") -> R.drawable.ic_interesting_place_point
        kinds.contains("amusements") -> R.drawable.ic_amusement_point
        kinds.contains("adult") -> R.drawable.ic_adult_point
        kinds.contains("accomodations") -> R.drawable.ic_accomodation_point
        else -> R.drawable.ic_undefined_point
    }
    val color = when {
        kinds.contains("tourist_facilities") -> colorLightDirtyGreen
        kinds.contains("sport") -> colorLightBlue
        kinds.contains("interesting_places") -> colorBlue
        kinds.contains("amusements") -> colorOrange
        kinds.contains("adult") -> colorRed
        kinds.contains("accomodations") -> colorPurple
        else -> colorBlue
    }
}
