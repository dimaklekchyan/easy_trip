package ru.klekchyan.easytrip.base_ui.theme

import androidx.compose.ui.graphics.Color

val colorBlue = Color(0xFF4C19DA)
val colorLightBlue = Color(0xFF4886FF)
val colorLightGray = Color(0x0606061A)
val colorDarkGreyBlue = Color(0xFF44475C)
val colorRed = Color(0xFFFF1012)
val colorLightDirtyGreen = Color(0xFFCBE957)
val colorOrange = Color(0xFFFF8818)
val colorPurple = Color(0xFF9C3BFB)
val colorDarkGray = Color(0xFF161717)
val colorGray = Color(0xFF252525)

data class Colors(
    val primaryColor: Color,
    val secondaryColor: Color,

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
    primaryColor = colorBlue,
    secondaryColor = colorDarkGreyBlue,
    primaryBackground = Color.White,
    secondaryBackground = Color.White,
    headerTextColor = Color.Black,
    primaryTextColor = Color.Black,
    primaryTextInvertColor = Color.White,
    hintTextColor = Color.DarkGray,
    primaryTintColor = Color.Gray,
    secondaryTintColor = Color.Blue
)

val darkPalette = Colors(
    primaryColor = colorBlue,
    secondaryColor = Color.White,
    primaryBackground = colorDarkGray,
    secondaryBackground = colorGray,
    headerTextColor = Color.White,
    primaryTextColor = Color.White,
    primaryTextInvertColor = Color.Black,
    hintTextColor = Color.LightGray,
    primaryTintColor = Color.Blue,
    secondaryTintColor = Color.LightGray
)