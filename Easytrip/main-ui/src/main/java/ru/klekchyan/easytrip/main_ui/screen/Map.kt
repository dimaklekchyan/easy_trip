package ru.klekchyan.easytrip.main_ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.main_ui.R
import ru.klekchyan.easytrip.main_ui.utils.PlaceMarkImageProvider
import ru.klekchyan.easytrip.main_ui.utils.toPoint
import ru.klekchyan.easytrip.main_ui.vm.MapController

@Composable
fun Map(
    modifier: Modifier = Modifier,
    mapController: MapController
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val isDarkTheme = isSystemInDarkTheme()

    val mapView by remember { mutableStateOf(context.createMapView(mapController, isDarkTheme, density)) }

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

private fun Context.createMapView(mapController: MapController, isDarkTheme: Boolean, density: Float): MapView {
    return MapView(this).apply {
        //TODO Create appropriate styles
        //map.setMapStyle(this@createMapView.getMapStyle(false))

        val clusterizedCollection = map.mapObjects.addClusterizedPlacemarkCollection(mapController)

        mapController.setNewMaxZoom(map.maxZoom)
        mapController.setNewMinZoom(map.minZoom)

        mapController.setOnAddPlaceMark { place, isClicked ->

            var listener: MapObjectTapListener = MapObjectTapListener { _, _ -> false }

            val placemarkMapObject = clusterizedCollection.addPlacemark(
                Point(place.latitude ?: 0.0, place.longitude ?: 0.0),
                PlaceMarkImageProvider.getInstance(
                    context = this@createMapView,
                    place = place,
                    density = density,
                    isDarkTheme = isDarkTheme,
                    isClicked = isClicked
                )
            ).apply {
                listener = MapObjectTapListener { mapObject, _ ->
                    mapController.onPlaceMarkClick(
                        context = this@createMapView,
                        place = mapObject.userData as SimplePlace,
                        placeMarkMapObject = mapObject as PlacemarkMapObject,
                        isDarkTheme = isDarkTheme
                    )
                    mapObject.setIcon(
                        PlaceMarkImageProvider.getInstance(
                            context = this@createMapView,
                            place = place,
                            density = density,
                            isDarkTheme = isDarkTheme,
                            isClicked = true
                        )
                    )
                    true
                }

                this.userData = place
                this.addTapListener(listener)
            }
            placemarkMapObject to listener
        }

//        mapController.setOnRemovePlaceMark { mapObject, listener ->
//            listener?.let {
//                //clusterizedCollection.removeTapListener(it)
//                clusterizedCollection.parent.removeTapListener(it)
//            }
//            mapObject?.let {
//                //clusterizedCollection.remove(it)
//                clusterizedCollection.parent.remove(it)
//            }
//        }

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
            clusterizedCollection.clusterPlacemarks(60.0, 16)
        }
        mapController.setOnMoveTo { point, zoom ->
            map.move(
                CameraPosition(point, zoom, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1.5f),
                null
            )
        }
    }
}