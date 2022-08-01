package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.PlacesRepository

class DeleteFavoritePlaceUseCase(
    private val placesRepository: PlacesRepository
) {
    suspend operator fun invoke(xid: String) = placesRepository.deleteFavoritePlace(xid)
}