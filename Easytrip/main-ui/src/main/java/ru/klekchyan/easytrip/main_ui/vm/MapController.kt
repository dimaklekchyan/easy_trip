package ru.klekchyan.easytrip.main_ui.vm

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.useCases.GetDetailedPlaceUseCase
import ru.klekchyan.easytrip.domain.useCases.GetPlacesByRadiusAndNameUseCase
import ru.klekchyan.easytrip.domain.useCases.GetPlacesByRadiusUseCase

class MapController(
    private val scope: CoroutineScope,
    private val getPlacesByRadiusUseCase: GetPlacesByRadiusUseCase,
    private val getPlacesByRadiusAndNameUseCase: GetPlacesByRadiusAndNameUseCase,
    private val getDetailedPlaceUseCase: GetDetailedPlaceUseCase,
): CameraListener {

    var currentPlaces by mutableStateOf<List<SimplePlace>>(listOf())
        private set

    var currentDetailedPlace by mutableStateOf<DetailedPlace?>(null)
        private set

    private var onAddPlaceMark: (place: SimplePlace) -> Unit = {}
    private var onDeletePlaceMarks: () -> Unit = {}

    fun setOnAddPlaceMark(callback: (place: SimplePlace) -> Unit) {
        onAddPlaceMark = callback
    }

    fun setOnDeletePlaceMarks(callback: () -> Unit) {
        onDeletePlaceMarks = callback
    }

    fun onPlaceMarkClick(place: SimplePlace) {
        getDetailedPlace(place.xid)
    }

    override fun onCameraPositionChanged(
        map: Map,
        pos: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished) {
            onDeletePlaceMarks()
            getPlacesByRadius(10000.0, pos.target.longitude, pos.target.latitude, null)
        }
    }

    private fun getPlacesByRadius(
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ) {
        scope.launch(Dispatchers.IO) {
            getPlacesByRadiusUseCase(radius, longitude, latitude, kinds).collect { state ->
                when(state) {
                    is GetPlacesByRadiusUseCase.State.Loading -> { /*TODO*/ }
                    is GetPlacesByRadiusUseCase.State.Error -> { /*TODO*/ }
                    is GetPlacesByRadiusUseCase.State.Success -> {
                        currentPlaces = state.places
                        withContext(Dispatchers.Main) {
                            state.places.forEach {
                                onAddPlaceMark(it)
                            }
                        }
                    }
                }
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
                        Log.d("TAG2", "getDetailedPlace success ${state.place}")
                        currentDetailedPlace = state.place
                    }
                }
            }
        }
    }
}