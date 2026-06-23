package com.sangtq.musicappkmp.feature.albumdetail.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview

private fun sampleTrack(id: Long) =
    Track(id, "Sample Song $id", "Sample Artist", 1, "Sample Album", 1, null, null, 200, false)

@Preview
@Composable
private fun AlbumDetailContentPreview() {
    AppTheme {
        AlbumDetailContent(
            state = AlbumDetailUiState(
                album = Album(
                    id = 1,
                    title = "Sample Album",
                    artistName = "Sample Artist",
                    coverUrl = null,
                    releaseDate = "2020-05-26",
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
