package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository

class GetPlacesByRadiusAndNameUseCase(
    private val placesRepository: PlacesRepository
) {

    operator fun invoke(
        radius: Double,
        name: String,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ) = placesRepository.getPlacesByRadiusAndNameFlow(name, radius, longitude, latitude, kinds).map {
        when {
            it.isLoading() -> State.Loading
            it.isError() -> State.Error(it.errorInfo)
            else -> State.Success(it.data!!)
        }
    }

    sealed class State {
        object Loading: State()
        class Error(val message: String): State()
        class Success(val places: List<SimplePlace>): State()
    }

}