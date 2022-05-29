package ru.klekchyan.easytrip.domain.entities

data class SimplePlace(
    val xid: String,
    val name: String,
    val kinds: String,
    val osm: String? = null,
    val wikidata: String? = null,
    val dist: Double? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
)
