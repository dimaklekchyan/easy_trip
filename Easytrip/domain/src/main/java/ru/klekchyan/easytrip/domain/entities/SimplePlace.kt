package ru.klekchyan.easytrip.domain.entities

import androidx.compose.ui.graphics.Color

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
    val color = when {
        kinds.contains("tourist_facilities") -> Color.Gray
        kinds.contains("sport") -> Color.Blue
        kinds.contains("interesting_places") -> Color.Yellow
        kinds.contains("amusements") -> Color.Magenta
        kinds.contains("adult") -> Color.Red
        kinds.contains("accomodations") -> Color.Green
        else -> Color.White
    }
}
