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
    val address: PlaceAddress? = null
)

data class PlaceAddress(
    val road: String? = null,
    val town: String? = null,
    val city: String? = null,
    val suburb: String? = null,
    val state: String? = null,
    val county: String? = null,
    val country: String? = null,
    val houseNumber: String? = null,
    val cityDistrict: String? = null,
    val stateDistrict: String? = null
)
