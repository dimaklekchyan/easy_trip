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
import ru.klekchyan.easytrip.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    getPlacesByRadiusUseCase: GetPlacesByRadiusUseCase,
    getPlacesByRadiusAndNameUseCase: GetPlacesByRadiusAndNameUseCase,
    getDetailedPlaceUseCase: GetDetailedPlaceUseCase,
    getCatalogUseCase: GetCatalogUseCase,
    getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase,
    getFavoritePlaceUseCase: GetFavoritePlaceUseCase,
    addFavoritePlaceUseCase: AddFavoritePlaceUseCase,
    deleteFavoritePlaceUseCase: DeleteFavoritePlaceUseCase
): ViewModel() {

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

//    fun navigateToDetailedPlaceScreen(xid: String) {
//        navigationManager.navigate()
//    }

    private fun observeSearchQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            searchQuery.debounce(500).collect { query ->
                mapController.onSearchQueryChanged(query)
            }
        }
    }
}