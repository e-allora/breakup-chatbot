package com.eallora.breakupchatbot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import com.eallora.breakupchatbot.ui.responsive.WindowSize

/**
 * Main theme for the Breakup Recovery Chatbot.
 */
@Composable
fun ChatbotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors = androidx.compose.material3.darkColorScheme(
            primary = ChatbotColors.primary,
            onPrimary = ChatbotColors.onPrimary,
            primaryContainer = ChatbotColors.primaryContainer,
            onPrimaryContainer = ChatbotColors.onPrimaryContainer,
            secondary = ChatbotColors.secondary,
            onSecondary = ChatbotColors.onSecondary,
            secondaryContainer = ChatbotColors.secondaryContainer,
            onSecondaryContainer = ChatbotColors.onSecondaryContainer,
            tertiary = ChatbotColors.tertiary,
            onTertiary = ChatbotColors.onTertiary,
            error = ChatbotColors.error,
            onError = ChatbotColors.onError,
            surface = ChatbotColors.surfaceDark,
            onSurface = ChatbotColors.onSurfaceDark,
            surfaceVariant = ChatbotColors.surfaceVariantDark,
            onSurfaceVariant = ChatbotColors.onSurfaceVariantDark,
            background = ChatbotColors.backgroundDark,
            onBackground = ChatbotColors.onBackgroundDark,
            outline = ChatbotColors.outline,
            outlineVariant = ChatbotColors.outlineVariant
        )
    } else {
        androidx.compose.material3.lightColorScheme(
            primary = ChatbotColors.primary,
            onPrimary = ChatbotColors.onPrimary,
            primaryContainer = ChatbotColors.primaryContainer,
            onPrimaryContainer = ChatbotColors.onPrimaryContainer,
            secondary = ChatbotColors.secondary,
            onSecondary = ChatbotColors.onSecondary,
            secondaryContainer = ChatbotColors.secondaryContainer,
            onSecondaryContainer = ChatbotColors.onSecondaryContainer,
            tertiary = ChatbotColors.tertiary,
            onTertiary = ChatbotColors.onTertiary,
            error = ChatbotColors.error,
            onError = ChatbotColors.onError,
            surface = ChatbotColors.surfaceLight,
            onSurface = ChatbotColors.onSurfaceLight,
            surfaceVariant = ChatbotColors.surfaceVariantLight,
            onSurfaceVariant = ChatbotColors.onSurfaceVariantLight,
            background = ChatbotColors.backgroundLight,
            onBackground = ChatbotColors.onBackgroundLight,
            outline = ChatbotColors.outline,
            outlineVariant = ChatbotColors.outlineVariant
        )
    }

    val windowSize = rememberWindowSize()

    CompositionLocalProvider(LocalWindowSize provides windowSize) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}

private val LocalWindowSize = androidx.compose.runtime.staticCompositionLocalOf { WindowSize.Compact(0.dp) }