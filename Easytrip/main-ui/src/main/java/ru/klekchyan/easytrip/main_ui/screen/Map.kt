package ru.klekchyan.easytrip.main_ui.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.main_ui.vm.MainViewModel

@Composable
fun Map(
    vm: MainViewModel
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density

    LaunchedEffect(Unit) {
        vm.mapController.setNewDensity(density)
    }

    val mapView by remember {
        mutableStateOf(
            MapView(context).apply {
                val clusterizedCollection = map.mapObjects.addClusterizedPlacemarkCollection(vm.mapController)

                vm.mapController.setOnAddPlaceMark { place ->

                    var listener: MapObjectTapListener = MapObjectTapListener { _, _ -> false }

                    val bitmap = drawPlaceMark(place)
                    clusterizedCollection.addPlacemark(
                        Point(place.latitude ?: 0.0, place.longitude ?: 0.0),
                        ImageProvider.fromBitmap(bitmap),
                    ).apply {

                        listener = MapObjectTapListener { mapObject, _ ->
                            vm.mapController.onPlaceMarkClick(mapObject.userData as SimplePlace)
                            Toast.makeText(context, (mapObject.userData as SimplePlace).name, Toast.LENGTH_SHORT).show()
                            true
                        }

                        this.userData = place
                        this.addTapListener(listener)
                    }
                    listener
                }

                vm.mapController.setOnDeletePlaceMarks {
                    clusterizedCollection.clear()
                }
                vm.mapController.setOnClusterPlaceMarks {
                    clusterizedCollection.clusterPlacemarks(60.0, 14)
                }
            }
        )
    }

    DisposableEffect(key1 = Unit) {
        mapView.onStart()
        mapView.map.addCameraListener(vm.mapController)

        //TODO add request of current location
        mapView.map.move(
            CameraPosition(Point(56.8519, 60.6122), 14f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )

        onDispose {
            mapView.map.mapObjects.clear()
            mapView.onStop()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { _ ->
                mapView
            },
            modifier = Modifier.fillMaxSize()
        )
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