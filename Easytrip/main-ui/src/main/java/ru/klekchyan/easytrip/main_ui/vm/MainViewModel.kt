package ru.klekchyan.easytrip.main_ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import ru.klekchyan.easytrip.domain.useCases.GetDetailedPlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetGeoNameUseCase
import ru.klekchyan.easytrip.domain.useCases.GetSimplePlacesUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val getGeoNameUseCase = GetGeoNameUseCase(mainRepository)
    private val getSimplePlacesUseCase = GetSimplePlacesUseCase(mainRepository)
    private val getDetailedPlaceUseCase = GetDetailedPlaceUseCase(mainRepository)

    init {
        getGeoName("Moscow")
        getSimplePlaces(1000.0, 60.6122, 56.8519)
        getDetailedPlace("W286786280")
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

    fun getSimplePlaces(
        radius: Double,
        longitude: Double,
        latitude: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getSimplePlacesUseCase(radius, longitude, latitude).collect { state ->
                when(state) {
                    is GetSimplePlacesUseCase.State.Loading -> {
                        Log.d("TAG2", "loading")
                    }
                    is GetSimplePlacesUseCase.State.Error -> {
                        Log.d("TAG2", "error")
                    }
                    is GetSimplePlacesUseCase.State.Success -> {
                        Log.d("TAG2", "success ${state.places.size}")
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
}