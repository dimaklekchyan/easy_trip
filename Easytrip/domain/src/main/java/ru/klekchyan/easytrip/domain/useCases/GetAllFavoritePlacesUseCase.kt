package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import javax.inject.Inject

class GetAllFavoritePlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke() = placesRepository.getAllFavoritePlacesFlow()
}