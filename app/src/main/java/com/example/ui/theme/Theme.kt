package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IosDarkColorScheme = darkColorScheme(
  primary = IosYellowAccent,
  onPrimary = IosDarkBackground,
  secondary = IosBlueAccent,
  onSecondary = IosTextPrimary,
  tertiary = IosGreenAccent,
  background = IosDarkBackground,
  onBackground = IosTextPrimary,
  surface = IosDarkSurface,
  onSurface = IosTextPrimary,
  surfaceVariant = IosDarkSurfaceVariant,
  onSurfaceVariant = IosTextSecondary,
  outline = IosDarkBorder
)

@Composable
fun SumitEditorTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = IosDarkColorScheme,
    typography = Typography,
    content = content
  )
}

