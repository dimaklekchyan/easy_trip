package ru.klekchyan.easytrip.main_ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.useCases.*
import ru.klekchyan.easytrip.main_ui.utils.getDeltaBetweenPoints

class DetailedPlaceModel(
    private val scope: CoroutineScope,
    private val getDetailedPlaceUseCase: GetDetailedPlaceUseCase,
    private val getFavoritePlaceUseCase: GetFavoritePlaceUseCase,
    private val addFavoritePlaceUseCase: AddFavoritePlaceUseCase,
    private val deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase,
    private val getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase
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
                        val userLocation = getCurrentUserLocationUseCase().first()
                        val distanceToUser: Double? = userLocation?.let {
                            getDeltaBetweenPoints(
                                point1 = Point(state.place.latitude ?: 0.0, state.place.longitude ?: 0.0),
                                point2 = Point(userLocation.latitude, userLocation.longitude)
                            )
                        }
                        withContext(Dispatchers.Main) {
                            currentPlace = state.place.copy(isFavorite = isFavorite, distanceToUser = distanceToUser)
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