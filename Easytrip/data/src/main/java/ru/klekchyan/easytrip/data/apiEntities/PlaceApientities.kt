package ru.klekchyan.easytrip.data.apiEntities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
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
    val voyage: String? = null,
    val url: String? = null,
    val otm: String,
    val sources: PlaceSource,
    val info: PlaceInfo? = null,
    val bbox: Bbox? = null,
    val point: GeoPoint? = null,
    val address: PlaceAddress? = null
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
        latitude = point?.latitude
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
data class PlaceSource(
    val geometry: String,
    val attributes: List<String>
)

@Serializable
data class PlaceInfo(
    val src: String,
    @SerialName("src_id")
    val srcId: Int,
    val descr: String,
    val image: String,
    @SerialName("img_width")
    val imageWidth: Int,
    @SerialName("img_height")
    val imageHeight: Int,
)

@Serializable
data class Bbox(
    val lon_min: Double,
    val lon_max: Double,
    val lat_min: Double,
    val lat_max: Double,
)

@Serializable
data class PlaceAddress(
    val road: String,
    val town: String,
    val state: String,
    val county: String,
    val country: String,
    val postcode: String,
    @SerialName("country_code")
    val countryCode: String,
    @SerialName("house_number")
    val houseNumber: String
)
