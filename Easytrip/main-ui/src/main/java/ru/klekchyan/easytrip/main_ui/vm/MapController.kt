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

class MapController(
    private val scope: CoroutineScope,
    private val getPlacesByRadiusUseCase: GetPlacesByRadiusUseCase,
    private val getPlacesByRadiusAndNameUseCase: GetPlacesByRadiusAndNameUseCase,
    private val getDetailedPlaceUseCase: GetDetailedPlaceUseCase,
    private val getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase
): CameraListener, ClusterListener, ClusterTapListener {

    private var density by mutableStateOf<Float>(1f)

    var currentDetailedPlace by mutableStateOf<DetailedPlace?>(null)
        private set
    private var userLocation by mutableStateOf<CurrentUserLocation?>(null)
    private var currentRadius by mutableStateOf<Double>(0.0)
    private var lastPoint by mutableStateOf<Point>(Point(0.0, 0.0))
    private var currentPoint by mutableStateOf<Point>(Point(0.0, 0.0))
    private var currentSearchQuery by mutableStateOf<String>("")
    private var currentKinds by mutableStateOf<String?>(null)

    var onMoveTo: (point: Point, zoom: Float) -> Unit = { _, _ -> }
        private set
    private var onAddPlaceMark: ((place: SimplePlace) -> MapObjectTapListener) = { MapObjectTapListener { _, _ -> false} }
    private var onDeletePlaceMarks: () -> Unit = {}
    private var onClusterPlaceMarks: () -> Unit = {}

    private var listeners by mutableStateOf<List<MapObjectTapListener>>(listOf())

    init {
        getCurrentUserLocation()
    }

    fun setOnMoveTo(callback: (point: Point, zoom: Float) -> Unit) {
        onMoveTo = callback
    }

    fun setOnAddPlaceMark(callback: (place: SimplePlace) -> MapObjectTapListener) {
        onAddPlaceMark = callback
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

    fun onSearchQueryChanged(search: String) {
        currentSearchQuery = search
        getPlacesByRadiusAndNAme(
            radius = currentRadius,
            name = currentSearchQuery,
            longitude = currentPoint.longitude,
            latitude = currentPoint.latitude,
            kinds = currentKinds
        )
    }

    fun moveToUserLocation() {
        userLocation?.let {
            onMoveTo(it.toPoint(), 14f)
        }
    }

    override fun onCameraPositionChanged(
        map: Map,
        pos: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if(finished) {
            currentPoint = pos.target
            currentRadius = 4000.0

            val delta = getDeltaBetweenPoints(currentPoint, lastPoint)

            if(delta > 1) {
                lastPoint = pos.target
                getNewPlaces()
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
        return true
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
        kinds: String?
    ) {
        scope.launch(Dispatchers.IO) {
            getPlacesByRadiusAndNameUseCase(radius, name, longitude, latitude, kinds).collect { state ->
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

    private fun saveTapListener(listener: MapObjectTapListener) {
        listeners.toMutableList().let { mutableList ->
            mutableList.add(listener)
            listeners = mutableList
        }
    }

    private fun deleteAllTapListeners() {
        listeners = listOf()
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
                            Log.d("TAG2", "${currentDetailedPlace}")
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentUserLocation() {
        scope.launch(Dispatchers.IO) {
            getCurrentUserLocationUseCase().collect { location ->
                withContext(Dispatchers.Main) {
                    userLocation = location
                    Log.d("TAG2", "location: $location")
                }
            }
        }
    }
}