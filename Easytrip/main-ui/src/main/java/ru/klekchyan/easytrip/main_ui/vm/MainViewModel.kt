package ru.klekchyan.easytrip.main_ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.domain.repositories.LocationRepository
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
import ru.klekchyan.easytrip.domain.useCases.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    placesRepository: PlacesRepository,
    locationRepository: LocationRepository
): ViewModel() {

    private val getPlacesByRadiusUseCase = GetPlacesByRadiusUseCase(placesRepository)
    private val getPlacesByRadiusAndNameUseCase = GetPlacesByRadiusAndNameUseCase(placesRepository)
    private val getDetailedPlaceUseCase = GetDetailedPlaceUseCase(placesRepository)
    private val getCatalogUseCase = GetCatalogUseCase(placesRepository)
    private val getCurrentUserLocationUseCase = GetCurrentUserLocationUseCase(locationRepository)
    private val getFavoritePlaceUseCase = GetFavoritePlaceUseCase(placesRepository)
    private val addFavoritePlaceUseCase = AddFavoritePlaceUseCase(placesRepository)
    private val deleteFavoritePlaceUseCase = DeleteFavoritePlaceUseCase(placesRepository)

    val detailedPlaceModel = DetailedPlaceModel(
        scope = viewModelScope,
        getDetailedPlaceUseCase = getDetailedPlaceUseCase,
        getFavoritePlaceUseCase = getFavoritePlaceUseCase,
        addFavoritePlaceUseCase = addFavoritePlaceUseCase,
        deleteFavoritePlaceUseCase = deleteFavoritePlaceUseCase,
        getCurrentUserLocationUseCase = getCurrentUserLocationUseCase
    )

    val mapController = MapController(
        scope = viewModelScope,
        getPlacesByRadiusUseCase = getPlacesByRadiusUseCase,
        getPlacesByRadiusAndNameUseCase = getPlacesByRadiusAndNameUseCase,
        getCurrentUserLocationUseCase = getCurrentUserLocationUseCase,
        onDetailedPlaceClick = { xid ->
            detailedPlaceModel.onDetailedPlaceClick(xid)
        }
    )

    val catalogFilterModel = CatalogFilterModel(
        scope = viewModelScope,
        mapController = mapController,
        getCatalogUseCase = getCatalogUseCase
    )

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: Flow<String> = _searchQuery

    init {
        observeSearchQuery()
    }

    fun onSearchQueryChanged(search: String) {
        viewModelScope.launch {
            _searchQuery.emit(search)
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            searchQuery.debounce(500).collect { query ->
                mapController.onSearchQueryChanged(query)
            }
        }
    }
}