package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import javax.inject.Inject

class GetFavoritePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(xid: String) = placesRepository.getFavoritePlace(xid)
}