package com.sangtq.musicappkmp.feature.artistdetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme

@Preview
@Composable
private fun ArtistDetailContentPreview() {
    AppTheme {
        ArtistDetailContent(
            state = ArtistDetailUiState(
                artist = Artist(id = 1, name = "Sample Artist", pictureUrl = null, albumCount = 10, fanCount = 123_456),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
