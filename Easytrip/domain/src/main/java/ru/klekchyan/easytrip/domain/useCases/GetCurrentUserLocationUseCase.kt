package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.LocationRepository

class GetCurrentUserLocationUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke() = locationRepository.getCurrentLocationFlow()
}