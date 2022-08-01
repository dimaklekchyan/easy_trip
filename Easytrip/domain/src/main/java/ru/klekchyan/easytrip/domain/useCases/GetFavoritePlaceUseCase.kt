package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.PlacesRepository

class GetFavoritePlaceUseCase(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(xid: String) = placesRepository.getFavoritePlace(xid)
}