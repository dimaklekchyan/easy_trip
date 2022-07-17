package ru.klekchyan.easytrip.main_ui.screen

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.klekchyan.easytrip.common.getBitmapFromVectorDrawable
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

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}

private fun Context.createMapView(mapController: MapController): MapView {
    return MapView(this).apply {
        val clusterizedCollection = map.mapObjects.addClusterizedPlacemarkCollection(mapController)

        mapController.setOnAddPlaceMark { place ->

            var listener: MapObjectTapListener = MapObjectTapListener { _, _ -> false }

            clusterizedCollection.addPlacemark(
                Point(place.latitude ?: 0.0, place.longitude ?: 0.0),
                ImageProvider.fromBitmap(this@createMapView.getBitmapFromVectorDrawable(place.icon)),
            ).apply {

                listener = MapObjectTapListener { mapObject, _ ->
                    mapController.onPlaceMarkClick(mapObject.userData as SimplePlace)
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