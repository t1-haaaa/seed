package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = OnEmeraldPrimary,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = AmberSecondary,
    onSecondary = OnAmberSecondary,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = AmethystTertiary,
    onTertiary = OnAmethystTertiary,
    tertiaryContainer = AmethystContainer,
    onTertiaryContainer = OnAmethystContainer,
    background = ObsidianBackground,
    onBackground = OnObsidianText,
    surface = ObsidianSurface,
    onSurface = OnObsidianText,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = OnObsidianMuted,
    surfaceContainerHigh = ObsidianSurfaceHigh,
    outline = ObsidianOutline,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldLightPrimary,
    onPrimary = OnEmeraldLightPrimary,
    primaryContainer = EmeraldLightContainer,
    onPrimaryContainer = OnEmeraldLightContainer,
    secondary = AmberLightSecondary,
    onSecondary = OnAmberLightSecondary,
    secondaryContainer = AmberLightContainer,
    onSecondaryContainer = OnAmberLightContainer,
    tertiary = AmethystLightTertiary,
    onTertiary = OnAmethystLightTertiary,
    tertiaryContainer = AmethystLightContainer,
    onTertiaryContainer = OnAmethystLightContainer,
    background = LightBackground,
    onBackground = OnLightText,
    surface = LightSurface,
    onSurface = OnLightText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = OnLightMuted,
    outline = LightOutline,
  )

val SeedCraftShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun SeedCraftTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = SeedCraftShapes,
    content = content,
  )
}
