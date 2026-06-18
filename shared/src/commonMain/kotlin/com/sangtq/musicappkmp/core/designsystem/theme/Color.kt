package com.sangtq.musicappkmp.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Bảng màu "musium" trích từ Figma (dark theme + accent teal/cyan).
 * Không hardcode màu trong Composable — dùng `MaterialTheme.colorScheme` (xem [AppTheme]).
 */
internal object AppColors {
    val Teal = Color(0xFF25C9DE)          // accent chính (nút play, highlight)
    val TealDark = Color(0xFF18A7BA)
    val OnTeal = Color(0xFF03191D)

    val Background = Color(0xFF0D0E10)    // nền gần đen
    val Surface = Color(0xFF16171A)       // card/sheet
    val SurfaceVariant = Color(0xFF1E2024)
    val OnBackground = Color(0xFFF5F5F7)
    val OnSurfaceVariant = Color(0xFFB6BAC0)

    val Secondary = Color(0xFF9AA0A6)     // text phụ
    val Outline = Color(0xFF2C2F34)
    val Error = Color(0xFFFF5449)
    val OnError = Color(0xFF120303)
}
