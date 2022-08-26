package ru.klekchyan.easytrip.base_ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.klekchyan.easytrip.base_ui.theme.AppTheme

@Composable
fun AppSystemBars(){
    val systemUiController = rememberSystemUiController()
    val darkIcons = !isSystemInDarkTheme()
    val color = AppTheme.colors.secondaryBackground

    LaunchedEffect(key1 = Unit) {
        systemUiController.setSystemBarsColor(
            color = color,
            darkIcons = darkIcons
        )
        systemUiController.systemBarsDarkContentEnabled = darkIcons
    }
}