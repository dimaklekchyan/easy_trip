package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository

class GetDetailedPlaceUseCase(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke(xid: String) = placesRepository.getDetailedPlaceFlow(xid).map {
        when {
            it.isLoading() -> State.Loading
            it.isError() -> State.Error(it.errorInfo)
            it.isSuccess() -> State.Success(it.data!!)
            else -> {}
        }
    }

    sealed class State {
        object Loading: State()
        class Error(val message: String): State()
        class Success(val place: DetailedPlace): State()
    }
}