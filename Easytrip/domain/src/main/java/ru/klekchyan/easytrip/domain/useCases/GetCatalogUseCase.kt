package ru.klekchyan.easytrip.domain.useCases

import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.domain.entities.Catalog
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import javax.inject.Inject

class GetCatalogUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) {
    operator fun invoke() = placesRepository.getCatalogFlow().map {
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