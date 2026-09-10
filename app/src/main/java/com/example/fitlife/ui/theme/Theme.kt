package com.example.fitlife.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ThemeLight,
    secondary = ThemeMediumLight,
    background = ThemeDarkest,
    surface = ThemeDark,
    surfaceVariant = ThemeMediumDark,
    onPrimary = ThemeDarkest,
    onSecondary = ThemeDarkest,
    onBackground = ThemeLightest,
    onSurface = ThemeLightest,
    onSurfaceVariant = ThemeLightest
)

private val LightColorScheme = lightColorScheme(
    primary = ThemeDarkest,
    secondary = ThemeDark,
    background = ThemeLightest,
    surface = ThemeLight,
    surfaceVariant = ThemeMediumLight,
    onPrimary = ThemeLightest,
    onSecondary = ThemeLightest,
    onBackground = ThemeDarkest,
    onSurface = ThemeDarkest,
    onSurfaceVariant = ThemeDarkest
)

@Composable
fun FitLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamic color to strictly keep the modern minimal fitness theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}