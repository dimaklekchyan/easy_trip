package ru.klekchyan.easytrip.main_ui.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.domain.entities.Catalog
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

    var catalog by mutableStateOf<Catalog?>(null)
        private set

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: Flow<String> = _searchQuery

    var currentKinds by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        getCatalog()
        observeSearchQuery()
    }

    fun onSearchQueryChanged(search: String) {
        viewModelScope.launch {
            _searchQuery.emit(search)
        }
    }

    fun onKindsChanged(kind: String) {
        currentKinds.toMutableList().let { mutableList ->
            if(mutableList.contains(kind)) {
                mutableList.remove(kind)
            } else {
                mutableList.add(kind)
            }
            currentKinds = mutableList
        }
        mapController.onKindsChanged(currentKinds)
    }

    private fun observeSearchQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            searchQuery.debounce(500).collect { query ->
                mapController.onSearchQueryChanged(query)
            }
        }
    }

    private fun getCatalog() {
        viewModelScope.launch(Dispatchers.IO) {
            getCatalogUseCase().collect { state ->
                when(state) {
                    is GetCatalogUseCase.State.Loading -> {}
                    is GetCatalogUseCase.State.Error -> {}
                    is GetCatalogUseCase.State.Success -> {
                        catalog = state.catalog
                    }
                }
            }
        }
    }
}