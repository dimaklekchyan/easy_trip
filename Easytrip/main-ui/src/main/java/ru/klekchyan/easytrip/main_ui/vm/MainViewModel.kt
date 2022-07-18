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
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import ru.klekchyan.easytrip.domain.useCases.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mainRepository: MainRepository,
    locationRepository: LocationRepository
): ViewModel() {

    private val getPlacesByRadiusUseCase = GetPlacesByRadiusUseCase(mainRepository)
    private val getPlacesByRadiusAndNameUseCase = GetPlacesByRadiusAndNameUseCase(mainRepository)
    private val getDetailedPlaceUseCase = GetDetailedPlaceUseCase(mainRepository)
    private val getCatalogUseCase = GetCatalogUseCase(mainRepository)
    private val getCurrentUserLocationUseCase = GetCurrentUserLocationUseCase(locationRepository)

    val mapController = MapController(
        scope = viewModelScope,
        getPlacesByRadiusUseCase = getPlacesByRadiusUseCase,
        getPlacesByRadiusAndNameUseCase = getPlacesByRadiusAndNameUseCase,
        getDetailedPlaceUseCase = getDetailedPlaceUseCase,
        getCurrentUserLocationUseCase = getCurrentUserLocationUseCase
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