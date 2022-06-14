package ru.klekchyan.easytrip.data.apiEntities

import kotlinx.serialization.Serializable
import ru.klekchyan.easytrip.domain.entities.Catalog
import ru.klekchyan.easytrip.domain.entities.CatalogChild

@Serializable
data class CatalogApiEntity(
    val name: String,
    val num: String,
    val children: List<CatalogChildApiEntity>
) {
    fun toDomain() = Catalog(
        name = name,
        num = num,
        children = children.map { it.toDomain() }
    )
}

@Serializable
data class CatalogChildApiEntity(
    val id: String,
    val name: String,
    val num: String,
    val children: List<CatalogChildApiEntity>? = null
) {
    fun toDomain(): CatalogChild = CatalogChild(
        id = id,
        name = name,
        num = num,
        children = children?.map { it.toDomain() }
    )
}


