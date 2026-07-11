package com.eallora.breakupchatbot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

private val fontName = GoogleFont("Inter")

val FontFamily = FontFamily(
    androidx.compose.ui.text.googlefonts.Fonts.GoogleFont(fontName)
)

val Typography = Typography(
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)