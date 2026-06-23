package com.sangtq.musicappkmp.feature.search.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview

private fun sampleTrack(id: Long) =
    Track(id, "Sample Song $id", "Sample Artist", 1, "Sample Album", 1, null, null, 200, false)

@Preview
@Composable
private fun SearchContentResultsPreview() {
    AppTheme {
        SearchContent(
            state = SearchUiState(query = "lo-fi", results = (1L..6L).map { sampleTrack(it) }, total = 6),
            onIntent = {},
            onOpenAlbum = {},
            onOpenArtist = {},
        )
    }
}

@Preview
@Composable
private fun SearchContentEmptyPreview() {
    AppTheme {
        SearchContent(state = SearchUiState(), onIntent = {}, onOpenAlbum = {}, onOpenArtist = {})
    }
}
