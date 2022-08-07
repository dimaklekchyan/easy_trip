package ru.klekchyan.easytrip

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.klekchyan.easytrip.base_ui.theme.EasyTripTheme
import ru.klekchyan.easytrip.common.LocationRequester

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
                ApplicationScreen()
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
}