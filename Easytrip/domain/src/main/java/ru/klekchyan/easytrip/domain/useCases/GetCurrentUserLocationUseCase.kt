package ru.klekchyan.easytrip.domain.useCases

import ru.klekchyan.easytrip.domain.repositories.LocationRepository
import javax.inject.Inject

class GetCurrentUserLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke() = locationRepository.getCurrentLocationFlow()
}