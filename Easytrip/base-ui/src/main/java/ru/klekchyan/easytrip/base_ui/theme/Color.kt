package ru.klekchyan.easytrip.base_ui.theme

import androidx.compose.ui.graphics.Color

data class Colors(
    val primaryBackground: Color,
    val secondaryBackground: Color,

    val headerTextColor: Color,
    val primaryTextColor: Color,
    val primaryTextInvertColor: Color,
    val hintTextColor: Color,

    val primaryTintColor: Color,
    val secondaryTintColor: Color,
)

val lightPalette = Colors(
    primaryBackground = Color.White,
    secondaryBackground = Color.Green,
    headerTextColor = Color.Black,
    primaryTextColor = Color.Black,
    primaryTextInvertColor = Color.White,
    hintTextColor = Color.DarkGray,
    primaryTintColor = Color.Gray,
    secondaryTintColor = Color.Blue
)

val darkPalette = Colors(
    primaryBackground = Color.DarkGray,
    secondaryBackground = Color.LightGray,
    headerTextColor = Color.White,
    primaryTextColor = Color.White,
    primaryTextInvertColor = Color.Black,
    hintTextColor = Color.LightGray,
    primaryTintColor = Color.Blue,
    secondaryTintColor = Color.LightGray
)