package com.sangtq.musicappkmp.feature.library.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import androidx.compose.ui.tooling.preview.Preview

private fun sampleTrack(id: Long) =
    Track(id, "Sample Song $id", "Sample Artist", 1, "Sample Album", 1, null, null, 200, false)

@Preview
@Composable
private fun LibraryContentPreview() {
    AppTheme {
        LibraryContent(
            state = LibraryUiState(
                liked = (1L..3L).map { sampleTrack(it) },
                recent = (4L..6L).map { sampleTrack(it) },
                playlists = listOf(UserPlaylist(1, "My Mix", 5, null), UserPlaylist(2, "Focus", 12, null)),
            ),
            onIntent = {},
            onOpenAlbum = {},
            onOpenArtist = {},
        )
    }
}

@Preview
@Composable
private fun UserPlaylistDetailContentPreview() {
    AppTheme {
        UserPlaylistDetailContent(
            state = UserPlaylistDetailUiState(name = "My Mix", tracks = (1L..5L).map { sampleTrack(it) }),
            onIntent = {},
            onBack = {},
        )
    }
}

@Preview
@Composable
private fun CreatePlaylistDialogPreview() {
    AppTheme {
        CreatePlaylistDialog(onCreate = {}, onDismiss = {})
    }
}

@Preview
@Composable
private fun AddToPlaylistDialogPreview() {
    AppTheme {
        AddToPlaylistDialog(
            trackTitle = "Sample Song",
            playlists = listOf(UserPlaylist(1, "My Mix", 5, null), UserPlaylist(2, "Focus", 12, null)),
            onPick = {},
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun AddToPlaylistDialogEmptyPreview() {
    AppTheme {
        AddToPlaylistDialog(
            trackTitle = "Sample Song",
            playlists = emptyList(),
            onPick = {},
            onDismiss = {},
        )
    }
}
