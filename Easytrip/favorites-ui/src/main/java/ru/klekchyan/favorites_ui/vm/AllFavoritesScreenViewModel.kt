package ru.klekchyan.favorites_ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.klekchyan.easytrip.common.getDeltaBetweenPoints
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.useCases.AddFavoritePlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.DeleteFavoritePlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetAllFavoritePlacesUseCase
import ru.klekchyan.easytrip.domain.useCases.GetCurrentUserLocationUseCase
import javax.inject.Inject

@HiltViewModel
class AllFavoritesScreenViewModel @Inject constructor(
    private val getAllFavoritePlacesUseCase: GetAllFavoritePlacesUseCase,
    private val addFavoritePlaceUseCase: AddFavoritePlaceUseCase,
    private val deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase,
    private val getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase
): ViewModel(){

    var favoritesPlaces by mutableStateOf<List<DetailedPlace>>(emptyList())
        private set
    private var currentUserLocation: CurrentUserLocation? = null

    init {
        getCurrentUserLocation()
        getFavoritePlaces()
    }

    fun addToFavorite(place: DetailedPlace) {
        viewModelScope.launch(Dispatchers.IO) {
            addFavoritePlaceUseCase(place)
        }
    }

    fun deleteFromFavorite(xid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteFavoritePlaceUseCase(xid)
        }
    }

    private fun getFavoritePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllFavoritePlacesUseCase().collect { places ->
                withContext(Dispatchers.Main) {
                    favoritesPlaces = places.sortedBy { place ->
                        getDeltaBetweenPoints(
                            latitude1 = place.latitude ?: 0.0,
                            longitude1 = place.longitude ?: 0.0,
                            latitude2 = currentUserLocation?.latitude ?: 0.0,
                            longitude2 = currentUserLocation?.longitude ?: 0.0
                        )
                    }
                }
            }
        }
    }

    private fun getCurrentUserLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            getCurrentUserLocationUseCase().collect { location ->
                withContext(Dispatchers.Main) {
                    currentUserLocation = location
                }
            }
        }
    }
}