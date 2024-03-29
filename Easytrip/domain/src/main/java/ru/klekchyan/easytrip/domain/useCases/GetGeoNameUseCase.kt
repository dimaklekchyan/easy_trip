package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.GeoName
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import javax.inject.Inject

class GetGeoNameUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(name: String) = placesRepository.getPlaceGeoNameFlow(name).map {
        when {
            it.isLoading() -> State.Loading
            it.isError() -> State.Error(it.errorInfo)
            else -> State.Success(it.data!!)
        }
    }

    sealed class State {
        object Loading: State()
        class Error(val message: String): State()
        class Success(val geoName: GeoName): State()
    }
}