package com.sangtq.musicappkmp.feature.player.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import com.sangtq.musicappkmp.core.playback.PlayableTrack
import androidx.compose.ui.tooling.preview.Preview

private val sampleState = PlayerUiState(
    track = PlayableTrack(1, "Sample Song", "Sample Artist", null, "https://example.com/p.mp3"),
    isPlaying = true,
    progress = 0.3f,
    positionMs = 9_000,
    durationMs = 30_000,
)

@Preview
@Composable
private fun PlayerContentPreview() {
    AppTheme {
        PlayerContent(state = sampleState, onBack = {}, onTogglePlayPause = {}, onSeek = {})
    }
}

@Preview
@Composable
private fun MiniPlayerPreview() {
    AppTheme {
        MiniPlayer(state = sampleState, onExpand = {}, onTogglePlayPause = {})
    }
}
