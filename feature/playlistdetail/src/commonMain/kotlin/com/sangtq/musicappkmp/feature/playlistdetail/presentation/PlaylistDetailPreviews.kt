package com.sangtq.musicappkmp.feature.playlistdetail.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview

private fun sampleTrack(id: Long) =
    Track(id, "Sample Song $id", "Sample Artist", 1, "Sample Album", 1, null, null, 200, false)

@Preview
@Composable
private fun PlaylistDetailContentPreview() {
    AppTheme {
        PlaylistDetailContent(
            state = PlaylistDetailUiState(
                playlist = Playlist(
                    id = 1,
                    title = "Sample Playlist",
                    description = "A handpicked mix",
                    coverUrl = null,
                    trackCount = 5,
                    tracks = (1L..5L).map { sampleTrack(it) },
                ),
            ),
            onIntent = {},
            onBack = {},
            onOpenArtist = {},
        )
    }
}
