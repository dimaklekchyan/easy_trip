package ru.klekchyan.easytrip.base_ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun EasyTripTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (darkTheme) darkPalette else lightPalette

    CompositionLocalProvider(
        LocalColorProvider provides colors,
        LocalShapesProvider provides shapes,
        LocalTypographyProvider provides typography,
        content = content
    )
}

object AppTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColorProvider.current

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapesProvider.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypographyProvider.current
}

val LocalColorProvider = staticCompositionLocalOf<Colors> {
    error("No default colors provided")
}

val LocalShapesProvider = staticCompositionLocalOf<Shapes> {
    error("No default shapes provided")
}

val LocalTypographyProvider = staticCompositionLocalOf<Typography> {
    error("No default typography provided")
}