package ru.klekchyan.easytrip.main_ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.useCases.AddFavoritePlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.DeleteFavoritePlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetDetailedPlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetFavoritePlaceUseCase

class DetailedPlaceModel(
    private val scope: CoroutineScope,
    private val getDetailedPlaceUseCase: GetDetailedPlaceUseCase,
    private val getFavoritePlaceUseCase: GetFavoritePlaceUseCase,
    private val addFavoritePlaceUseCase: AddFavoritePlaceUseCase,
    private val deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase
) {

    var currentPlace by mutableStateOf<DetailedPlace?>(null)
        private set

    fun onDetailedPlaceClick(xid: String) {
        getDetailedPlace(xid)
    }

    fun addToFavorite() {
        scope.launch(Dispatchers.IO) {
            currentPlace?.let { place ->
                addFavoritePlaceUseCase(place)
            }
            withContext(Dispatchers.Main) {
                currentPlace = currentPlace?.copy(isFavorite = true)
            }
        }
    }

    fun deleteFromFavorite() {
        scope.launch(Dispatchers.IO) {
            currentPlace?.let { place ->
                deleteFavoritePlaceUseCase(place.xid)
            }
            withContext(Dispatchers.Main) {
                currentPlace = currentPlace?.copy(isFavorite = false)
            }
        }
    }

    private fun getDetailedPlace(xid: String) {
        scope.launch(Dispatchers.IO) {
            getDetailedPlaceUseCase(xid).collect { state ->
                when(state) {
                    is GetDetailedPlaceUseCase.State.Loading -> { /*TODO*/ }
                    is GetDetailedPlaceUseCase.State.Error -> { /*TODO*/ }
                    is GetDetailedPlaceUseCase.State.Success -> {
                        val isFavorite = checkFavoritePlace(state.place.xid)
                        withContext(Dispatchers.Main) {
                            currentPlace = state.place.copy(isFavorite = isFavorite)
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkFavoritePlace(xid: String): Boolean {
        return getFavoritePlaceUseCase(xid).first() != null
    }

    fun onCloseDetailedPlaceSheet() {
        currentPlace = null
    }
}