package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import javax.inject.Inject

class DeleteFavoritePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    suspend operator fun invoke(xid: String) = placesRepository.deleteFavoritePlace(xid)
}