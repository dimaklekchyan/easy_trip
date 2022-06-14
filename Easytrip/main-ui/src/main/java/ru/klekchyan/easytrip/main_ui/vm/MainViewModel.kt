package ru.klekchyan.easytrip.main_ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.domain.entities.Catalog
import ru.klekchyan.easytrip.domain.entities.CatalogChild
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import ru.klekchyan.easytrip.domain.useCases.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val getGeoNameUseCase = GetGeoNameUseCase(mainRepository)
    private val getPlacesByRadiusUseCase = GetPlacesByRadiusUseCase(mainRepository)
    private val getPlacesByRadiusAndNameUseCase = GetPlacesByRadiusAndNameUseCase(mainRepository)
    private val getDetailedPlaceUseCase = GetDetailedPlaceUseCase(mainRepository)
    private val getCatalogUseCase = GetCatalogUseCase(mainRepository)

    val mapController = MapController(
        scope = viewModelScope,
        getPlacesByRadiusUseCase = getPlacesByRadiusUseCase,
        getPlacesByRadiusAndNameUseCase = getPlacesByRadiusAndNameUseCase,
        getDetailedPlaceUseCase = getDetailedPlaceUseCase
    )

    val categories = mutableListOf<String>()

    init {
        //getGeoName("Ekaterinburg")
        //getPlacesByRadius(10000.0, 60.6122, 56.8519, "wall_painting")
//        getPlacesByRadiusAndName("Высоц", 5000.0, 60.6122, 56.8519, "skyscrapers")
        //getDetailedPlace("W286786280")
        getCatalog()
    }

    fun getCatalog() {
        viewModelScope.launch(Dispatchers.IO) {
            getCatalogUseCase().collect { state ->
                when(state) {
                    is GetCatalogUseCase.State.Loading -> {}
                    is GetCatalogUseCase.State.Error -> {}
                    is GetCatalogUseCase.State.Success -> {
                        getAllFinishedCategories(state.catalog.children)
//                        categories.forEach {
//                            Log.d("TAG2", it)
//                        }
                        Log.d("TAG2", "${categories.size}")
                    }
                }
            }
        }
    }

    fun getGeoName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getGeoNameUseCase(name = name).collect { state ->
                when(state) {
                    is GetGeoNameUseCase.State.Loading -> {
                        Log.d("TAG2", "loading")
                    }
                    is GetGeoNameUseCase.State.Error -> {
                        Log.d("TAG2", "error")
                    }
                    is GetGeoNameUseCase.State.Success -> {
                        Log.d("TAG2", "success ${state.geoName}")
                    }
                }
            }
        }
    }

    fun getPlacesByRadius(
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getPlacesByRadiusUseCase(radius, longitude, latitude, kinds).collect { state ->
                when(state) {
                    is GetPlacesByRadiusUseCase.State.Loading -> {
                        Log.d("TAG2", "loading")
                    }
                    is GetPlacesByRadiusUseCase.State.Error -> {
                        Log.d("TAG2", "error")
                    }
                    is GetPlacesByRadiusUseCase.State.Success -> {
                        Log.d("TAG2", "success ${state.places.size}")
                        state.places.forEach {
                            Log.d("TAG2", "$it")
                        }
                    }
                }
            }
        }
    }

    fun getPlacesByRadiusAndName(
        name: String,
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getPlacesByRadiusAndNameUseCase(name, radius, longitude, latitude, kinds).collect { state ->
                when(state) {
                    is GetPlacesByRadiusAndNameUseCase.State.Loading -> {
                        Log.d("TAG2", "loading")
                    }
                    is GetPlacesByRadiusAndNameUseCase.State.Error -> {
                        Log.d("TAG2", "error")
                    }
                    is GetPlacesByRadiusAndNameUseCase.State.Success -> {
//                        state.places.filter { it.name.isNotEmpty() }.forEach {
//                            Log.d("TAG2", "$it")
//                        }
                    }
                }
            }
        }
    }

    fun getDetailedPlace(xid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getDetailedPlaceUseCase(xid).collect { state ->
                when(state) {
                    is GetDetailedPlaceUseCase.State.Loading -> {
                        Log.d("TAG2", "getDetailedPlace loading")
                    }
                    is GetDetailedPlaceUseCase.State.Error -> {
                        Log.d("TAG2", "getDetailedPlace error")
                    }
                    is GetDetailedPlaceUseCase.State.Success -> {
                        Log.d("TAG2", "getDetailedPlace success ${state.place}")
                    }
                }
            }
        }
    }

    private fun getAllFinishedCategories(children: List<CatalogChild>) {
        children.forEach {
            if (it.children.isNullOrEmpty()) {
                Log.d("TAG2", "Add ${it.id}")
                categories.add(it.id)
            } else {
                Log.d("TAG2", "Go deeper ${it.id}")
                getAllFinishedCategories(it.children!!)
            }
        }
    }
}