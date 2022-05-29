package ru.klekchyan.easytrip.domain.entities

data class DetailedPlace(
    val xid: String,
    val name: String,
    val kinds: String,
    val osm: String? = null,
    val wikidata: String? = null,
    val rate: String,
    val imageUrl: String? = null,
    val previewUrl: String? = null,
    val wikipediaUrl: String? = null,
    val wikipediaTitle: String? = null,
    val wikipediaText: String? = null,
    val wikipediaHtml: String? = null,
    val url: String? = null,
    val otm: String,
    val longitude: Double? = null,
    val latitude: Double? = null,
)
