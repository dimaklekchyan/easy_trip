package ru.klekchyan.easytrip

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.klekchyan.easytrip.base_ui.theme.EasyTripTheme
import ru.klekchyan.easytrip.common.LocationRequester
import ru.klekchyan.easytrip.main_ui.screen.MainScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity(), LocationRequester {

    private var mService: LocationService? = null
    private var mBound = false
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mService = null
            mBound = false
        }
    }

    override fun requestLocationUpdates() {
        mService?.requestLocationUpdates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)

        setContent {
            EasyTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainScreen(hiltViewModel())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        bindService(
            LocationService.createIntent(this),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        if (mBound) {
            unbindService(mServiceConnection)
            mBound = false
        }
        super.onStop()
    }

    companion object {
        private const val EXTRA_DATA_KEY = "extra_data_key"

        fun createIntent(context: Context, fromNotification: Boolean): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_DATA_KEY, fromNotification)
            }
        }
    }
}