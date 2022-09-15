package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import javax.inject.Inject

class AddFavoritePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    suspend operator fun invoke(place: DetailedPlace) = placesRepository.addFavoritePlace(place)
}