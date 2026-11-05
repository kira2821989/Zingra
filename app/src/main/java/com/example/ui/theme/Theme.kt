package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ZingraDarkColorScheme = darkColorScheme(
  primary = ZingraCyan,
  onPrimary = ZingraBackground,
  primaryContainer = ZingraIndigo,
  onPrimaryContainer = ZingraCyanLight,
  secondary = ZingraViolet,
  onSecondary = ZingraBackground,
  tertiary = ZingraGreen,
  background = ZingraBackground,
  onBackground = ZingraTextPrimary,
  surface = ZingraSurface,
  onSurface = ZingraTextPrimary,
  surfaceVariant = ZingraSurfaceVariant,
  onSurfaceVariant = ZingraTextSecondary,
  outline = ZingraBorder,
  error = ZingraRed
)

private val ZingraLightColorScheme = lightColorScheme(
  primary = ZingraIndigo,
  onPrimary = ZingraTextPrimary,
  primaryContainer = ZingraCyanLight,
  onPrimaryContainer = ZingraBackground,
  secondary = ZingraViolet,
  tertiary = ZingraGreen,
  background = ZingraBackground,
  onBackground = ZingraTextPrimary,
  surface = ZingraSurface,
  onSurface = ZingraTextPrimary,
  surfaceVariant = ZingraSurfaceVariant,
  onSurfaceVariant = ZingraTextSecondary,
  outline = ZingraBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek dark cinematic theme for Zingra
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) ZingraDarkColorScheme else ZingraLightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = ZingraBackground.toArgb()
        window.navigationBarColor = ZingraSurface.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
