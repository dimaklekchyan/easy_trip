package ru.klekchyan.easytrip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.AndroidEntryPoint
import ru.klekchyan.easytrip.base_ui.theme.EasyTripTheme
import ru.klekchyan.easytrip.main_ui.screen.MainScreen
import ru.klekchyan.easytrip.main_ui.screen.Map
import ru.klekchyan.easytrip.main_ui.screen.NewMap
import ru.klekchyan.easytrip.main_ui.screen.rememberMapState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        setContent {
            EasyTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
//                    val mapState = rememberMapState()
//                    LaunchedEffect(key1 = mapState.isReady) {
//                        if(mapState.isReady) {
//                            mapState.setPosition(Point(58.7002, 59.4839), 6f, Animation.Type.SMOOTH, 400f)
//                        }
//                        Log.d("TAG2", "${mapState.currentPosition?.latitude} ${mapState.currentPosition?.longitude}")
//                    }
                    Map(hiltViewModel())
                    //MainScreen(vm = hiltViewModel())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}