package ru.klekchyan.easytrip.main_ui.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.useCases.*
import ru.klekchyan.easytrip.main_ui.utils.ClusterImageProvider
import ru.klekchyan.easytrip.main_ui.utils.getDeltaBetweenPoints
import ru.klekchyan.easytrip.main_ui.utils.toPoint
import ru.klekchyan.easytrip.main_ui.utils.toRequestFormat

class MapController(
    private val scope: CoroutineScope,
    private val getPlacesByRadiusUseCase: GetPlacesByRadiusUseCase,
    private val getPlacesByRadiusAndNameUseCase: GetPlacesByRadiusAndNameUseCase,
    private val getDetailedPlaceUseCase: GetDetailedPlaceUseCase,
    private val getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase
): CameraListener, ClusterListener, ClusterTapListener {

    private var density by mutableStateOf(1f)

    private var userLocation by mutableStateOf<CurrentUserLocation?>(null)
    private var moveToUserWhenLocationWillBeReceived = false
    private var drawUserPlacemark = false
    private var currentRadius by mutableStateOf(0.0)
    private var lastPoint by mutableStateOf(Point(0.0, 0.0))
    private var currentPoint by mutableStateOf(Point(0.0, 0.0))
    private var currentZoom by mutableStateOf(14f)
    var currentSearchQuery by mutableStateOf("")
        private set
    private var currentKinds by mutableStateOf<List<String>>(emptyList())
    var currentDetailedPlace by mutableStateOf<DetailedPlace?>(null)
        private set

    private var onMoveTo: (point: Point, zoom: Float) -> Unit = { _, _ -> }
    private var onAddPlaceMark: ((place: SimplePlace) -> MapObjectTapListener) = { MapObjectTapListener { _, _ -> false} }
    private var onAddUserPlaceMark: (location: CurrentUserLocation, mapObject: PlacemarkMapObject?) -> PlacemarkMapObject? = { _, _ -> null }
    private var onDeletePlaceMarks: () -> Unit = {}
    private var onClusterPlaceMarks: () -> Unit = {}

    private var listeners by mutableStateOf<List<MapObjectTapListener>>(listOf())
    private var userPlacemarkMapObject by mutableStateOf<PlacemarkMapObject?>(null)

    init {
        getCurrentUserLocation()
    }

    fun setOnMoveTo(callback: (point: Point, zoom: Float) -> Unit) {
        onMoveTo = callback
    }

    fun setOnAddPlaceMark(callback: (place: SimplePlace) -> MapObjectTapListener) {
        onAddPlaceMark = callback
    }

    fun setOnAddUserPlaceMark(callback: (location: CurrentUserLocation, mapObject: PlacemarkMapObject?) -> PlacemarkMapObject) {
        onAddUserPlaceMark = callback
    }

    fun setOnDeletePlaceMarks(callback: () -> Unit) {
        onDeletePlaceMarks = callback
    }

    fun setOnClusterPlaceMarks(callback: () -> Unit) {
        onClusterPlaceMarks = callback
    }

    fun onPlaceMarkClick(place: SimplePlace) {
        getDetailedPlace(place.xid)
    }

    fun setNewDensity(value: Float) {
        density = value
    }

    override fun onCameraPositionChanged(
        map: Map,
        pos: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished) {
            currentPoint = pos.target
            currentZoom = pos.zoom
            currentRadius = 4000.0

            val delta = getDeltaBetweenPoints(currentPoint, lastPoint)

            if(delta > 1) {
                lastPoint = pos.target
                getNewPlaces()
            }
        }
    }

    override fun onClusterAdded(cluster: Cluster) {
        cluster.appearance.setIcon(
            ClusterImageProvider.getInstance(
                size = cluster.size,
                density = density
            )
        )
        cluster.addClusterTapListener(this)
    }

    override fun onClusterTap(cluster: Cluster): Boolean {
        //TODO
        return true
    }

    fun onSearchQueryChanged(search: String) {
        currentSearchQuery = search
        getNewPlaces()
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
        getNewPlaces()
    }

    fun moveToUserLocation() {
        if(userLocation != null) {
            onMoveTo(userLocation!!.toPoint(), currentZoom)
            drawUserPlacemark = true
            moveToUserWhenLocationWillBeReceived = false
        } else {
            moveToUserWhenLocationWillBeReceived = true
        }
    }

    private fun getCurrentUserLocation() {
        scope.launch(Dispatchers.IO) {
            getCurrentUserLocationUseCase().collect { location ->
                withContext(Dispatchers.Main) {
                    userLocation = location
                    userLocation?.let {
                        if(moveToUserWhenLocationWillBeReceived) {
                            moveToUserLocation()
                        }
                        if(drawUserPlacemark) {
                            userPlacemarkMapObject =
                                onAddUserPlaceMark(userLocation!!, userPlacemarkMapObject)
                        }
                    }
                }
            }
        }
    }

    private fun getNewPlaces() {
        if (currentSearchQuery.isNotEmpty()) {
            getPlacesByRadiusAndNAme(
                radius = currentRadius,
                name = currentSearchQuery,
                longitude = currentPoint.longitude,
                latitude = currentPoint.latitude,
                kinds = currentKinds
            )
        } else {
            getPlacesByRadius(
                radius = currentRadius,
                longitude = currentPoint.longitude,
                latitude = currentPoint.latitude,
                kinds = currentKinds
            )
        }
    }

    private fun getPlacesByRadius(
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: List<String>
    ) {
        scope.launch(Dispatchers.IO) {
            getPlacesByRadiusUseCase(radius, longitude, latitude, kinds.toRequestFormat()).collect { state ->
                when(state) {
                    is GetPlacesByRadiusUseCase.State.Loading -> { /*TODO*/ }
                    is GetPlacesByRadiusUseCase.State.Error -> { /*TODO*/ }
                    is GetPlacesByRadiusUseCase.State.Success -> {
                        onGetPlaces(state.places)
                    }
                }
            }
        }
    }

    private fun getPlacesByRadiusAndNAme(
        radius: Double,
        name: String,
        longitude: Double,
        latitude: Double,
        kinds: List<String>
    ) {
        scope.launch(Dispatchers.IO) {
            getPlacesByRadiusAndNameUseCase(radius, name, longitude, latitude, kinds.toRequestFormat()).collect { state ->
                when(state) {
                    is GetPlacesByRadiusAndNameUseCase.State.Loading -> { /*TODO*/ }
                    is GetPlacesByRadiusAndNameUseCase.State.Error -> { /*TODO*/ }
                    is GetPlacesByRadiusAndNameUseCase.State.Success -> {
                        onGetPlaces(state.places)
                    }
                }
            }
        }
    }

    private suspend fun onGetPlaces(places: List<SimplePlace>) {
        withContext(Dispatchers.Main) {
            onDeletePlaceMarks()
            deleteAllTapListeners()
            places.forEach { place ->
                val listener = onAddPlaceMark(place)
                saveTapListener(listener)
            }
            onClusterPlaceMarks()
        }
    }

    private fun getDetailedPlace(xid: String) {
        scope.launch(Dispatchers.IO) {
            getDetailedPlaceUseCase(xid).collect { state ->
                when(state) {
                    is GetDetailedPlaceUseCase.State.Loading -> { /*TODO*/ }
                    is GetDetailedPlaceUseCase.State.Error -> { /*TODO*/ }
                    is GetDetailedPlaceUseCase.State.Success -> {
                        withContext(Dispatchers.Main) {
                            currentDetailedPlace = state.place
                        }
                    }
                }
            }
        }
    }

    private fun saveTapListener(listener: MapObjectTapListener) {
        listeners.toMutableList().let { mutableList ->
            mutableList.add(listener)
            listeners = mutableList
        }
    }

    private fun deleteAllTapListeners() {
        listeners = listOf()
    }
}