package ru.klekchyan.easytrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import ru.klekchyan.easytrip.base_ui.theme.EasyTripTheme
import ru.klekchyan.easytrip.main_ui.screen.MainScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var mapView: MapView? = null

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        setContent {
            EasyTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
//                    MapScreen(
//                        mapView = mapView,
//                        onMapCreated = {
//                            mapView = it
//                        }
//                    )
                    MainScreen(vm = hiltViewModel())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}