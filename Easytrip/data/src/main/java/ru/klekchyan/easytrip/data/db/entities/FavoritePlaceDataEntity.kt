package ru.klekchyan.easytrip.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.klekchyan.easytrip.data.db.typeConverters.StringListTypeConverter
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.PlaceAddress

@Entity(tableName = "favorite_places")
@TypeConverters(StringListTypeConverter::class)
data class FavoritePlaceDataEntity(
    @PrimaryKey
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
    val houseNumber: String? = null,
    val road: String? = null,
    val town: String? = null,
    val cityDistrict: String? = null,
    val city: String? = null,
    val suburb: String? = null,
    val state: String? = null,
    val county: String? = null,
    val country: String? = null,
    val stateDistrict: String? = null
) {
    fun toDomain() = DetailedPlace(
        xid = xid,
        name = name,
        description = description,
        kinds = kinds,
        osm = osm,
        wikidata = wikidata,
        rate = rate,
        imageUrl = imageUrl,
        previewUrl = previewUrl,
        wikipediaUrl = wikipediaUrl,
        wikipediaTitle = wikipediaTitle,
        wikipediaText = wikipediaText,
        wikipediaHtml = wikipediaHtml,
        url = url,
        otm = otm,
        longitude = longitude,
        latitude = latitude,
        address = PlaceAddress(
            houseNumber = houseNumber ?: "",
            road = road ?: "",
            town = town ?: "",
            cityDistrict = cityDistrict ?: "",
            city = city ?: "",
            suburb = suburb ?: "",
            state = state ?: "",
            county = county ?: "",
            country = country ?: "",
            stateDistrict = stateDistrict ?: ""
        ),
        isFavorite = true
    )
}

fun DetailedPlace.toDataEntity() = FavoritePlaceDataEntity(
    xid = xid,
    name = name,
    description = description,
    kinds = kinds,
    osm = osm,
    wikidata = wikidata,
    rate = rate,
    imageUrl = imageUrl,
    previewUrl = previewUrl,
    wikipediaUrl = wikipediaUrl,
    wikipediaTitle = wikipediaTitle,
    wikipediaText = wikipediaText,
    wikipediaHtml = wikipediaHtml,
    url = url,
    otm = otm,
    longitude = longitude,
    latitude = latitude,
    houseNumber = address?.houseNumber,
    road = address?.road ?: "",
    town = address?.town ?: "",
    cityDistrict = address?.cityDistrict ?: "",
    city = address?.city ?: "",
    suburb = address?.suburb ?: "",
    state = address?.state ?: "",
    county = address?.county ?: "",
    country = address?.country ?: "",
    stateDistrict = address?.stateDistrict ?: ""
)
