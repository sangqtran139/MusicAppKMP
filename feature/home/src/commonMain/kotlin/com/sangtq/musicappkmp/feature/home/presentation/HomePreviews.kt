package com.sangtq.musicappkmp.feature.home.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import com.sangtq.musicappkmp.feature.home.domain.model.FeaturedPlaylist
import com.sangtq.musicappkmp.feature.home.domain.model.HomeSection
import androidx.compose.ui.tooling.preview.Preview

private fun sampleTrack(id: Long) =
    Track(id, "Sample Song $id", "Sample Artist", 1, "Sample Album", 1, null, null, 200, false)

@Preview
@Composable
private fun HomeContentPreview() {
    AppTheme {
        HomeContent(
            state = HomeUiState(
                playlists = listOf(
                    FeaturedPlaylist(1, "Top Worldwide", null),
                    FeaturedPlaylist(2, "Top USA", null),
                ),
                sections = listOf(
                    HomeSection("Top Hits", (1L..5L).map { sampleTrack(it) }),
                    HomeSection("Pop", (6L..10L).map { sampleTrack(it) }),
                ),
            ),
            onIntent = {},
            onOpenAlbum = {},
            onOpenPlaylist = {},
            recent = (11L..14L).map { sampleTrack(it) },
        )
    }
}
