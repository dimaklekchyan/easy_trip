package ru.klekchyan.easytrip

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("d4673e54-fd7e-422c-a962-ad0610c5e691")
    }
}