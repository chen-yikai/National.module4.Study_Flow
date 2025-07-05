package com.example.nationalmodule4

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nationalmodule4.room.RecordDataModal

val LocalNavController = compositionLocalOf<NavHostController> { error("LocalNavController") }
val LocalRecordDataModal = compositionLocalOf<RecordDataModal> { error("LocalRecordModal") }

// Legacy fixed max width (kept for compatibility)
val maxWidth: Dp = 800.dp

/**
 * Calculates responsive max width based on screen size
 * - Phone (<600dp): Full width
 * - Small tablet (600-840dp): 90% of screen width
 * - Large tablet/desktop (>840dp): Fixed 800dp
 */
@Composable
fun getResponsiveMaxWidth(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> screenWidth
        screenWidth < 840.dp -> (screenWidth * 0.9f)
        else -> 800.dp
    }
}

/**
 * Calculates responsive max width with custom breakpoints
 */
@Composable
fun getResponsiveMaxWidth(
    phoneMaxWidth: Dp? = null,
    tabletPercentage: Float = 0.9f,
    desktopMaxWidth: Dp = 800.dp
): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> phoneMaxWidth ?: screenWidth
        screenWidth < 840.dp -> (screenWidth * tabletPercentage)
        else -> desktopMaxWidth
    }
}
