package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.PlacesRepository

class GetAllFavoritePlacesUseCase(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke() = placesRepository.getAllFavoritePlacesFlow()
}