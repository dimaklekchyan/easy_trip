package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.Catalog
import ru.klekchyan.easytrip.domain.repositories.MainRepository

class GetCatalogUseCase(
    private val mainRepository: MainRepository
) {
    operator fun invoke() = mainRepository.getCatalogFlow().map {
        when {
            it.isLoading() -> State.Loading
            it.isError() -> State.Error(it.errorInfo)
            else -> State.Success(it.data!!)
        }
    }

    sealed class State {
        object Loading: State()
        class Error(val message: String): State()
        class Success(val catalog: Catalog): State()
    }
}