package ru.klekchyan.easytrip

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import ru.klekchyan.easytrip.common.requestingLocationUpdates
import ru.klekchyan.easytrip.common.setRequestingLocationUpdates

class LocalBinder(val service: LocationService) : Binder()

@AndroidEntryPoint
class LocationService(

): Service() {

    private val channelId = "channel_01"

    private val extraStartedFromNotification = pckgName +
            ".started_from_notification"

    private val mBinder: IBinder = LocalBinder(this)

    private val updateIntervalInMilliseconds: Long = 10000

    private val fastestUpdateIntervalInMilliseconds = updateIntervalInMilliseconds / 2

    private val notificationId = 12345678

    private var mChangingConfiguration = false

    private var mNotificationManager: NotificationManager? = null

    private var mLocationRequest: LocationRequest? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mLocationCallback: LocationCallback? = null

    private var mServiceHandler: Handler? = null

    private var mLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG2", "service onCreate")
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
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val name: CharSequence = "Навигация"
        val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)

        mNotificationManager!!.createNotificationChannel(mChannel)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("TAG2", "service onStartCommand")
        val startedFromNotification = intent.getBooleanExtra(
            extraStartedFromNotification,
            false
        )

        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("TAG2", "service onBind")
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.d("TAG2", "service onRebind")
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("TAG2", "service onUnbind")
        if (!mChangingConfiguration && this.requestingLocationUpdates()) {
            startForeground(notificationId, getNotification())
        }
        return true
    }

    override fun onDestroy() {
        Log.d("TAG2", "service onDestroy")
        mServiceHandler?.removeCallbacksAndMessages(null)
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

    private fun removeLocationUpdates() {
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            this.setRequestingLocationUpdates(false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            this.setRequestingLocationUpdates(true)
        }
    }

    private fun getNotification(): Notification {

        val intent = MainActivity.createIntent(this, true)

        val activityPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .addAction(R.drawable.ic_launcher_foreground, "Перейти в приложение", activityPendingIntent)
            .setContentText("Ваше местоположение отправляется.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())


        builder.setChannelId(channelId)
        return builder.build()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(OnCompleteListener<Location> { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
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
        const val pckgName = "ru.klekchyan.easytrip"

        fun createIntent(context: Context): Intent {
            return Intent(context, LocationService::class.java)
        }
    }
}