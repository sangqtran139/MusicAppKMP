package com.sangtq.musicappkmp.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing

/** Loading/Empty/Error states dùng chung để UX nhất quán giữa các màn (Phase 5 polish). */

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier)
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Text(
        message,
        modifier = modifier.padding(AppSpacing.md),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(AppSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Text(
            "Tap to retry",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onRetry).padding(AppSpacing.sm),
        )
    }
}
