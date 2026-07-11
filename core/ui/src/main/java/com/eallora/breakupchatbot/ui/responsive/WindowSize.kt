package com.eallora.breakupchatbot.ui.responsive

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window size classes for responsive design.
 */
sealed class WindowSize {
    abstract val width: Dp

    data class Compact(override val width: Dp) : WindowSize()
    data class Medium(override val width: Dp) : WindowSize()
    data class Expanded(override val width: Dp) : WindowSize()
}

/**
 * Remember the current window size for responsive layout decisions.
 */
@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return when (val widthDp = configuration.screenWidthDp.dp) {
        in Dp.MinValue..599.dp -> WindowSize.Compact(widthDp)
        in 600.dp..839.dp -> WindowSize.Medium(widthDp)
        else -> WindowSize.Expanded(widthDp)
    }
}

/**
 * Check if the current window size is compact (phone).
 */
val WindowSize.isCompact: Boolean
    get() = this is WindowSize.Compact

/**
 * Check if the current window size is medium (small tablet).
 */
val WindowSize.isMedium: Boolean
    get() = this is WindowSize.Medium

/**
 * Check if the current window size is expanded (large tablet).
 */
val WindowSize.isExpanded: Boolean
    get() = this is WindowSize.Expanded