package ru.klekchyan.easytrip.main_ui.screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.klekchyan.easytrip.common.LocationRequester
import ru.klekchyan.easytrip.common.checkLocationPermissions
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.main_ui.R
import ru.klekchyan.easytrip.main_ui.utils.toPoint
import ru.klekchyan.easytrip.main_ui.vm.MapController

@Composable
fun Map(
    modifier: Modifier = Modifier,
    mapController: MapController
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        map.forEach { (_, isGranted) ->
            if (isGranted) {
                (context as LocationRequester).requestLocationUpdates()
                mapController.moveToUserLocation()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        context.checkLocationPermissions(
            onFineGranted = {
                (context as LocationRequester).requestLocationUpdates()
                mapController.moveToUserLocation()
            },
            onCoarseGranted = {
                (context as LocationRequester).requestLocationUpdates()
                mapController.moveToUserLocation()
            },
            onAllDenied = {}
        )
    }

    val mapView by remember { mutableStateOf(context.createMapView(mapController)) }

    DisposableEffect(key1 = Unit) {
        mapView.onStart()
        mapView.map.addCameraListener(mapController)
        mapController.setNewDensity(density)

        onDispose {
            mapView.map.mapObjects.clear()
            mapView.onStop()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                context.checkLocationPermissions(
                    onFineGranted = {
                        (context as LocationRequester).requestLocationUpdates()
                        mapController.moveToUserLocation()
                    },
                    onCoarseGranted = {
                        (context as LocationRequester).requestLocationUpdates()
                        mapController.moveToUserLocation()
                        launcher.launch(arrayOf(ACCESS_FINE_LOCATION))
                    },
                    onAllDenied = {
                        launcher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
                    }
                )
            }
        ) {
            Text(text = "Текущее местоположение")
        }
    }
}

internal fun Context.createMapView(mapController: MapController): MapView {
    return MapView(this).apply {
        val clusterizedCollection = map.mapObjects.addClusterizedPlacemarkCollection(mapController)

        mapController.setOnAddPlaceMark { place ->

            var listener: MapObjectTapListener = MapObjectTapListener { _, _ -> false }

            val bitmap = drawPlaceMark(place)
            clusterizedCollection.addPlacemark(
                Point(place.latitude ?: 0.0, place.longitude ?: 0.0),
                ImageProvider.fromBitmap(bitmap),
            ).apply {

                listener = MapObjectTapListener { mapObject, _ ->
                    mapController.onPlaceMarkClick(mapObject.userData as SimplePlace)
                    Toast.makeText(context, (mapObject.userData as SimplePlace).name, Toast.LENGTH_SHORT).show()
                    true
                }

                this.userData = place
                this.addTapListener(listener)
            }
            listener
        }

        mapController.setOnAddUserPlaceMark { location, oldMapObject ->
            oldMapObject?.let {
                map.mapObjects.remove(oldMapObject)
            }
            map.mapObjects.addPlacemark(location.toPoint(), ImageProvider.fromResource(context, R.drawable.search_result))
        }

        mapController.setOnDeletePlaceMarks {
            clusterizedCollection.clear()
        }
        mapController.setOnClusterPlaceMarks {
            clusterizedCollection.clusterPlacemarks(60.0, 14)
        }
        mapController.setOnMoveTo { point, zoom ->
            map.move(
                CameraPosition(point, zoom, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 2f),
                null
            )
        }
    }
}

fun drawPlaceMark(place: SimplePlace): Bitmap {
    val picSize = 100.dp.value.toInt()
    val bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    // отрисовка плейсмарка
    val paint = Paint()
    paint.color = Color.WHITE
    paint.style = Paint.Style.FILL
    canvas.drawCircle(picSize / 2f, picSize / 2f, picSize / 2f, paint)
    paint.color = Color.BLACK
    paint.style = Paint.Style.STROKE
    canvas.drawCircle(picSize / 2f, picSize / 2f, picSize / 2f, paint)
    // отрисовка текста
    paint.color = Color.BLACK;
    paint.isAntiAlias = true;
    paint.textSize = 14.sp.value
    paint.textAlign = Paint.Align.CENTER;
    canvas.drawText(place.name, picSize / 2f,
        picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
    return bitmap
}