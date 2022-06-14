package ru.klekchyan.easytrip.main_ui.screen

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider

class MapState(
    private val context: Context,
    val onPositionChanged: (Point) -> Unit = {},
) : CameraListener {
    var currentPosition: Point? by mutableStateOf(null)
        private set

    var locationChangeListener: (Point) -> Unit = {}
        private set


    fun onLocationChange(l: (Point) -> Unit) {
        locationChangeListener = l
        if (currentPosition != null) {
            locationChangeListener(currentPosition!!)
        }
    }

    var isReady by mutableStateOf(false)
        private set

    fun ready() {
        isReady = true
    }

    fun stop() {
        isReady = false
    }

    private var onAddPlacemark: (point: Point, userData: Any?, imageProvider: ImageProvider) -> Unit =
        { _, _, _ -> }

    internal fun setOnAddPlacemark(l: (point: Point, userData: Any?, imageProvider: ImageProvider) -> Unit) {
        onAddPlacemark = l
    }

    private var onAddPlacemarks: (point: MutableList<Pair<Int, Point>>, imageProvider: ImageProvider) -> Unit =
        { _, _ -> }

    internal fun setOnAddPlacemarks(l: (points: MutableList<Pair<Int, Point>>, imageProvider: ImageProvider) -> Unit) {
        onAddPlacemarks = l
    }

    private fun setCurrentLocation(location: Point) {
        currentPosition = location
        locationChangeListener(currentPosition!!)
    }

    fun addPlaceMark(point: Point, userData: Any? = null, imageProvider: ImageProvider) {
        onAddPlacemark(point, userData, imageProvider)
    }

    fun addPlaceMarks(points: MutableList<Pair<Int, Point>>, imageProvider: ImageProvider) {
        onAddPlacemarks(points, imageProvider)
    }

    private fun checkLocationSetting(
        context: Context,
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit
    ) {

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)

        val gpsSettingTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener { onEnabled() }
        gpsSettingTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution)
                        .build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore here
                }
            }
        }

    }

    var onSetPosition: (point: Point, zoom: Float, animationType: Animation.Type, duration: Float) -> Unit =
        { _, _, _, _ -> }
        private set

    fun setOnSetPositionHandler(l: (point: Point, zoom: Float, animationType: Animation.Type, duration: Float) -> Unit) {
        onSetPosition = l
    }

    fun setPosition(
        point: Point,
        zoom: Float = 14f,
        animationType: Animation.Type = Animation.Type.LINEAR,
        duration: Float = 0.8f
    ) {
        currentPosition = point
        onSetPosition(point, zoom, animationType, duration)
    }

    override fun onCameraPositionChanged(
        map: Map,
        pos: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        currentPosition = pos.target
        onPositionChanged(currentPosition!!)
    }
}

@Composable
fun rememberMapState(
    position: Point? = null,
    onPositionChanged: (Point) -> Unit = {}
): MapState {
    val context = LocalContext.current
    return remember {
        MapState(context = context, onPositionChanged = onPositionChanged)
    }
}