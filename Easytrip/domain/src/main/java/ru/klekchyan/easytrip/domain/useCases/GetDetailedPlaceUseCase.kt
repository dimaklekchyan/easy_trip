package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.repositories.MainRepository

class GetDetailedPlaceUseCase(
    private val mainRepository: MainRepository
) {
    operator fun invoke(xid: String) = mainRepository.getDetailedPlaceFlow(xid).map {
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
        class Success(val place: DetailedPlace): State()
    }
}