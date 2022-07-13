package ru.klekchyan.easytrip.domain.entities

data class DetailedPlace(
    val xid: String,
    val name: String,
    val description: String,
    //Comma-separated list of categories
    val kinds: List<String>,
    //OpenStreetMap identifier of the object
    val osm: String,
    //Wikidata identifier of the object
    val wikidata: String,
    //Rating of the object popularity
    val rate: String,
    val imageUrl: String,
    val previewUrl: String,
    val wikipediaUrl: String,
    //Page title in wikipedia
    val wikipediaTitle: String,
    //Plain-text extract
    val wikipediaText: String,
    //Limited HTML extract
    val wikipediaHtml: String,
    //Link to website
    val url: String,
    //Link to object at opentripmap.com
    val otm: String,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val address: PlaceAddress? = null
)

data class PlaceAddress(
    val houseNumber: String,
    val road: String,
    val town: String,
    val cityDistrict: String,
    val city: String,
    val suburb: String,
    val state: String,
    val county: String,
    val country: String,
    val stateDistrict: String
)
