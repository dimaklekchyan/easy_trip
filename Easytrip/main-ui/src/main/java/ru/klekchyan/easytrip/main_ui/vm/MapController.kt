package ru.klekchyan.easytrip.main_ui.vm

import android.content.Context
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
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.useCases.GetCurrentUserLocationUseCase
import ru.klekchyan.easytrip.domain.useCases.GetPlacesByRadiusAndNameUseCase
import ru.klekchyan.easytrip.domain.useCases.GetPlacesByRadiusUseCase
import ru.klekchyan.easytrip.main_ui.utils.*

data class PlaceMark(
    val place: SimplePlace,
    val mapObject: PlacemarkMapObject?,
    val listener: MapObjectTapListener?,
    val isClicked: Boolean
)

class MapController(
    private val scope: CoroutineScope,
    private val getPlacesByRadiusUseCase: GetPlacesByRadiusUseCase,
    private val getPlacesByRadiusAndNameUseCase: GetPlacesByRadiusAndNameUseCase,
    private val getCurrentUserLocationUseCase: GetCurrentUserLocationUseCase,
    private val onDetailedPlaceClick: (xid: String) -> Unit
): CameraListener, ClusterListener, ClusterTapListener {

    private var density by mutableStateOf(1f)

    private var userLocation by mutableStateOf<CurrentUserLocation?>(null)
    private var moveToUserWhenLocationWillBeReceived = false
    private var drawUserPlacemark = false
    private var currentRadius by mutableStateOf(0.0)
    private var lastPoint by mutableStateOf(Point(0.0, 0.0))
    private var currentPoint by mutableStateOf(Point(0.0, 0.0))
    private var currentZoom by mutableStateOf(0f)
    private var maxZoom by mutableStateOf(0f)
    private var minZoom by mutableStateOf(0f)
    var currentSearchQuery by mutableStateOf("")
        private set
    private var currentKinds by mutableStateOf<List<String>>(emptyList())

    private var onMoveTo: (point: Point, zoom: Float) -> Unit = { _, _ -> }
    private var onAddPlaceMark: ((place: SimplePlace, isClicked: Boolean) -> Pair<PlacemarkMapObject?, MapObjectTapListener?>) = { _, _ -> null to null }
//    private var onRemovePlaceMark: (mapObject: PlacemarkMapObject?, listener: MapObjectTapListener?) -> Unit = { _, _ -> }
    private var onAddUserPlaceMark: (location: CurrentUserLocation, mapObject: PlacemarkMapObject?) -> PlacemarkMapObject? = { _, _ -> null }
    private var onDeletePlaceMarks: () -> Unit = {}
    private var onClusterPlaceMarks: () -> Unit = {}

    private var listeners by mutableStateOf<List<MapObjectTapListener>>(listOf())
    private var userPlacemarkMapObject by mutableStateOf<PlacemarkMapObject?>(null)
    private var clickedPlaceMarkObject by mutableStateOf<PlacemarkMapObject?>(null)
    private var clickedPlace by mutableStateOf<SimplePlace?>(null)

//    private var placeMarks: MutableList<PlaceMark> = mutableListOf()

    init {
        getCurrentUserLocation()
    }

    fun setOnMoveTo(callback: (point: Point, zoom: Float) -> Unit) {
        onMoveTo = callback
    }

    fun setOnAddPlaceMark(callback: (place: SimplePlace, isClicked: Boolean) -> Pair<PlacemarkMapObject?, MapObjectTapListener?>) {
        onAddPlaceMark = callback
    }

//    fun setOnRemovePlaceMark(callback: (mapObject: PlacemarkMapObject?, listener: MapObjectTapListener?) -> Unit) {
//        onRemovePlaceMark = callback
//    }

    fun setOnAddUserPlaceMark(callback: (location: CurrentUserLocation, mapObject: PlacemarkMapObject?) -> PlacemarkMapObject) {
        onAddUserPlaceMark = callback
    }

    fun setOnDeletePlaceMarks(callback: () -> Unit) {
        onDeletePlaceMarks = callback
    }

    fun setOnClusterPlaceMarks(callback: () -> Unit) {
        onClusterPlaceMarks = callback
    }

    fun onPlaceMarkClick(
        context: Context,
        place: SimplePlace,
        placeMarkMapObject: PlacemarkMapObject,
        isDarkTheme: Boolean
    ) {
        onClearClickedPlace(isDarkTheme, context)
        onDetailedPlaceClick(place.xid)
        clickedPlace = place
        clickedPlaceMarkObject = placeMarkMapObject
    }

    fun setNewDensity(value: Float) {
        density = value
    }

    fun setNewMaxZoom(value: Float) {
        maxZoom = value
    }

    fun setNewMinZoom(value: Float) {
        minZoom = value
        currentZoom = minZoom
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

            if(delta > 1000) {
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
        onMoveTo(cluster.appearance.geometry, (currentZoom + 1).coerceAtMost(maxZoom))
        return true
    }

    fun onSearchQueryChanged(search: String) {
        currentSearchQuery = search
        getNewPlaces()
    }

    fun onKindsChanged(kinds: List<String>) {
        currentKinds = kinds
        getNewPlaces()
    }

    fun moveToUserLocation() {
        if(userLocation != null) {
            onMoveTo(userLocation!!.toPoint(), currentZoom.coerceAtLeast(MIN_CURRENT_LOCATION_ZOOM))
            drawUserPlacemark = true
            moveToUserWhenLocationWillBeReceived = false
        } else {
            moveToUserWhenLocationWillBeReceived = true
        }
    }

    fun increaseZoom() {
        onMoveTo(currentPoint, (currentZoom + 1).coerceAtMost(maxZoom))
    }

    fun decreaseZoom() {
        onMoveTo(currentPoint, (currentZoom - 1).coerceAtLeast(minZoom))
    }

    fun onClearClickedPlace(isDarkTheme: Boolean, context: Context) {
        try {
            clickedPlace?.let { place ->
                clickedPlaceMarkObject?.setIcon(
                    PlaceMarkImageProvider.getInstance(
                        context = context,
                        place = place,
                        density = density,
                        isDarkTheme = isDarkTheme,
                        isClicked = false
                    )
                )
            }
            clickedPlace = null
            clickedPlaceMarkObject = null
        } catch (e: RuntimeException) {
            Log.d("TAG2", "error: ${e.cause}")
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

    private suspend fun onGetPlaces(newPlaces: List<SimplePlace>) {
        withContext(Dispatchers.Main) {
            onDeletePlaceMarks()
            deleteAllTapListeners()
            newPlaces.forEach { place ->
                val (_, listener) = onAddPlaceMark(place, false)
                saveTapListener(listener)
            }
            clickedPlace?.let { place ->
                val (mapObject, listener) = onAddPlaceMark(place, true)
                clickedPlaceMarkObject = mapObject
                saveTapListener(listener)
            }
            onClusterPlaceMarks()
        }

//        withContext(Dispatchers.Default) {
//            val placesToRemove = placeMarks.filter { !it.isClicked }.map { it.place }.minus(newPlaces.toSet())
//            val placesToAdd = newPlaces.minus(placeMarks.map { it.place }.toSet())
//
//            withContext(Dispatchers.Main) {
//                placesToAdd.forEach { place ->
//                    val (mapObject, listener) = onAddPlaceMark(place, false)
//                    placeMarks.add(PlaceMark(place, mapObject, listener, false))
//                }
//                placesToRemove.forEach { place ->
//                    val placeMark = placeMarks.firstOrNull { it.place.xid == place.xid }
//                    placeMark?.let {
//                        onRemovePlaceMark(placeMark.mapObject, placeMark.listener)
//                    }
//                }
//                onClusterPlaceMarks()
//            }
//        }
    }

    private fun saveTapListener(listener: MapObjectTapListener?) {
        listeners.toMutableList().let { mutableList ->
            listener?.let {
                mutableList.add(listener)
                listeners = mutableList
            }
        }
    }

    private fun deleteAllTapListeners() {
        listeners = listOf()
    }

    companion object {
        private const val MIN_CURRENT_LOCATION_ZOOM = 14F
    }
}