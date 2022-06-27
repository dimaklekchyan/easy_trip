package ru.klekchyan.easytrip

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.delay
import ru.klekchyan.easytrip.main_ui.vm.MapController


//Just example of using LocationManager

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapController: MapController,
    mapView: MapView?
) {
    val locationManager = getSystemService(LocalContext.current, LocationManager::class.java) as LocationManager

    val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(key1 = mapView) {
        if(locationPermissionState.allPermissionsGranted) {
            if(hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0f
                ) {
                    mapView?.map?.move(
                        CameraPosition(Point(it.latitude, it.longitude),10f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0f),
                        null
                    )
                }
            }
            if(hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0f
                ) {
                    mapView?.map?.move(
                        CameraPosition(Point(it.latitude, it.longitude),10f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0f),
                        null
                    )
                }
            }
        }
    }

    SideEffect {
        if(!locationPermissionState.allPermissionsGranted) {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }
}
