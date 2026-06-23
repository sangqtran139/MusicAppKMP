package com.sangtq.musicappkmp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MusiumColorScheme = darkColorScheme(
    primary = AppColors.Teal,
    onPrimary = AppColors.OnTeal,
    primaryContainer = AppColors.TealDark,
    onPrimaryContainer = AppColors.OnBackground,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnBackground,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnBackground,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    outline = AppColors.Outline,
    error = AppColors.Error,
    onError = AppColors.OnError,
)

/** Theme gốc của app. Bọc toàn bộ UI ở `:shared` (NavHost). */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MusiumColorScheme,
        typography = AppTypography,
        content = content,
    )
}
