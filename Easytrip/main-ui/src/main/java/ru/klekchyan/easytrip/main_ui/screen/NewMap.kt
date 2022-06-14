package ru.klekchyan.easytrip.main_ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.mapview.MapView

@Composable
fun NewMap(
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    val mapView by remember {
        mutableStateOf(
            MapView(context).apply {

            }
        )
    }

    DisposableEffect(key1 = Unit) {
        mapView.onStart()

        onDispose {
            mapView.onStop()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView }
    )
}