package com.sangtq.musicappkmp.feature.playlistdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.component.AppAsyncImage
import com.sangtq.musicappkmp.core.designsystem.component.ErrorState
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing

@Composable
fun PlaylistDetailContent(
    state: PlaylistDetailUiState,
    onIntent: (PlaylistDetailIntent) -> Unit,
    onBack: () -> Unit,
    onOpenArtist: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Text(
                "←",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable(onClick = onBack),
            )
            Text(
                state.playlist?.title ?: "Playlist",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> ErrorState(
                    message = state.error,
                    onRetry = { onIntent(PlaylistDetailIntent.Retry) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.playlist != null -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { PlaylistHeader(state.playlist) }
                    items(state.playlist.tracks, key = { it.id }) { track ->
                        TrackRow(
                            track = track,
                            onClick = { onIntent(PlaylistDetailIntent.TrackClicked(track)) },
                            onArtistClick = track.artistId?.let { id -> { onOpenArtist(id) } },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistHeader(playlist: Playlist) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(AppSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        AppAsyncImage(
            url = playlist.coverUrl,
            contentDescription = playlist.title,
            modifier = Modifier.size(200.dp).clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Text(
            playlist.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        playlist.description?.takeIf { it.isNotBlank() }?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            "${playlist.trackCount} songs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TrackRow(track: Track, onClick: () -> Unit, onArtistClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppAsyncImage(
            url = track.coverUrl,
            contentDescription = track.title,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Column(Modifier.weight(1f)) {
            Text(
                track.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                track.artistName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = if (onArtistClick != null) Modifier.clickable(onClick = onArtistClick) else Modifier,
            )
        }
    }
}
