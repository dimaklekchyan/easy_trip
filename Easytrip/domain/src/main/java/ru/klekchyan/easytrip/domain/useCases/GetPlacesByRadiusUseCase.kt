package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.repositories.MainRepository

class GetPlacesByRadiusUseCase(
    private val mainRepository: MainRepository
) {
    operator fun invoke(
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ) = mainRepository.getPlacesByRadiusFlow(radius, longitude, latitude, kinds).map {
        when {
            it.isLoading() -> {
                State.Loading
            }
            it.isError() -> {
                State.Error(it.errorInfo)
            }
            else -> {
                State.Success(it.data!!)
            }
        }
    }

    sealed class State {
        object Loading: State()
        class Error(val message: String): State()
        class Success(val places: List<SimplePlace>): State()
    }
}