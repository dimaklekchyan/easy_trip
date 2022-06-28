package ru.klekchyan.easytrip.domain.entities

data class SimplePlace(
    val xid: String,
    val name: String,
    //Comma-separated list of categories
    val kinds: String,
    //OpenStreetMap identifier of the object
    val osm: String? = null,
    //Wikidata identifier of the object
    val wikidata: String? = null,
    //Distance in meters from selected point (for radius query)
    val dist: Double? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
)
