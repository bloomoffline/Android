package com.lab.tb.distributed.chat.android.presentation.component.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Light theme color scheme
 */
val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = White50,
    secondary = White50,
    onSecondary = Grey500,
    surface = White50,
    onSurface = Black900,
)

/**
 * Dark theme color scheme
 */
val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = White300,
    secondary = Grey600,
    onSecondary = Grey500,
    surface = Black900,
    onSurface = White300,
)

/**
 * Distributed Chat App theme.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 */
@Composable
fun DistributedChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Color scheme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Background theme
    val backgroundTheme = BackgroundTheme(color = colorScheme.surface)

    val tintTheme = TintTheme()

    // Composition locals
    CompositionLocalProvider(
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = DistributedChatAppTypography,
            content = content,
        )
    }
}
