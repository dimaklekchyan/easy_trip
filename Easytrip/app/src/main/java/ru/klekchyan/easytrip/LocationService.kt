package ru.klekchyan.easytrip

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.klekchyan.easytrip.common.setRequestingLocationUpdates
import ru.klekchyan.easytrip.domain.repositories.LocationRepository
import javax.inject.Inject

class LocalBinder(val service: LocationService) : Binder()

@AndroidEntryPoint
class LocationService(): Service() {

    @Inject
    lateinit var locationRepository: LocationRepository

    private val mBinder: IBinder = LocalBinder(this)

    private val updateIntervalInMilliseconds: Long = 10000

    private val fastestUpdateIntervalInMilliseconds = updateIntervalInMilliseconds / 2

    private var mChangingConfiguration = false

    private var mLocationRequest: LocationRequest? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mLocationCallback: LocationCallback? = null

    private var mServiceHandler: Handler? = null

    private var mLocation: Location? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }
        createLocationRequest()
        getLastLocation()
        val handlerThread = HandlerThread("handler_tag")
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        scope.cancel()
        return true
    }

    override fun onDestroy() {
        mServiceHandler?.removeCallbacksAndMessages(null)
        scope.cancel()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        this.setRequestingLocationUpdates(true)
        startService(Intent(applicationContext, LocationService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback!!, Looper.myLooper()!!
            )
        } catch (unlikely: SecurityException) {
            this.setRequestingLocationUpdates(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(OnCompleteListener<Location> { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                        scope.launch {
                            locationRepository.saveLocation(
                                latitude = task.result?.latitude ?: 0.0,
                                longitude = task.result?.longitude ?: 0.0,
                                isPrecise = false)
                        }
                    } else {
                        //log.e("Failed to get location.")
                    }
                })
        } catch (unlikely: SecurityException) {
            //log.e("Lost location permission.$unlikely")
        }
    }

    private fun onNewLocation(location: Location) {
        mLocation = location
        Log.d("TAG2", "service onNewLocation ${location.latitude} ${location.longitude}")
        scope.launch {
            locationRepository.saveLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                isPrecise = false)
        }
        //TODO send location to some repository
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create().apply {
            interval = updateIntervalInMilliseconds
            fastestInterval = fastestUpdateIntervalInMilliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LocationService::class.java)
        }
    }
}