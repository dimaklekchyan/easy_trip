package ru.klekchyan.easytrip.data.apiEntities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.PlaceAddress
import ru.klekchyan.easytrip.domain.entities.SimplePlace

@Serializable
data class SimplePlaceApiEntity(
    val xid: String,
    val name: String,
    val kinds: String,
    val osm: String? = null,
    val wikidata: String? = null,
    val dist: Double? = null,
    val point: GeoPoint? = null,
    val rate: Int? = null
) {
    fun toDomain() = SimplePlace(
        xid = xid,
        name = name,
        kinds = kinds,
        osm = osm,
        wikidata = wikidata,
        dist = dist,
        longitude = point?.longitude,
        latitude = point?.latitude
    )
}

@Serializable
data class DetailedPlaceApiEntity(
    val xid: String,
    val name: String,
    val kinds: String,
    val osm: String? = null,
    val wikidata: String? = null,
    val rate: String,
    val image: String? = null,
    val preview: PlacePreview? = null,
    val wikipedia: String? = null,
    @SerialName("wikipedia_extracts")
    val wikipediaExtracts: WikipediaExtracts? = null,
    val url: String? = null,
    val otm: String,
    val point: GeoPoint? = null,
    val address: PlaceAddressApiEntity? = null
) {
    fun toDomain() = DetailedPlace(
        xid = xid,
        name = name,
        kinds = kinds,
        osm = osm,
        wikidata = wikidata,
        rate = rate,
        imageUrl = image,
        previewUrl = preview?.source,
        wikipediaUrl = wikipedia,
        wikipediaTitle = wikipediaExtracts?.title,
        wikipediaText = wikipediaExtracts?.text,
        wikipediaHtml = wikipediaExtracts?.html,
        url = url,
        otm = otm,
        longitude = point?.longitude,
        latitude = point?.latitude,
        address = address?.toDomain()
    )
}

@Serializable
data class GeoPoint(
    @SerialName("lon")
    val longitude: Double,
    @SerialName("lat")
    val latitude: Double
)

@Serializable
data class PlacePreview(
    val source: String,
    val height: Int,
    val width: Int
)

@Serializable
data class WikipediaExtracts(
    val title: String,
    val text: String,
    val html: String
)

@Serializable
data class PlaceAddressApiEntity(
    val road: String? = null,
    val town: String? = null,
    val city: String? = null,
    val suburb: String? = null,
    val state: String? = null,
    val county: String? = null,
    val country: String? = null,
    @SerialName("house_number")
    val houseNumber: String? = null,
    @SerialName("city_district")
    val cityDistrict: String? = null,
    @SerialName("state_district")
    val stateDistrict: String? = null
) {
    fun toDomain() = PlaceAddress(
        road, town, city, suburb, state, county, country, houseNumber, cityDistrict, stateDistrict
    )
}
