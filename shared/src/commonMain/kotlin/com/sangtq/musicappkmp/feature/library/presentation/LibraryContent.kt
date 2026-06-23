package com.sangtq.musicappkmp.feature.library.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.component.AppAsyncImage
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing

@Composable
fun LibraryContent(
    state: LibraryUiState,
    onIntent: (LibraryIntent) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    onOpenArtist: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().safeContentPadding(),
        contentPadding = PaddingValues(horizontal = AppSpacing.md, vertical = AppSpacing.md),
    ) {
        item {
            Text(
                "Your Library",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = AppSpacing.md),
            )
        }

        item { SectionHeader("Liked Songs (${state.liked.size})") }
        if (state.liked.isEmpty()) {
            item { EmptyHint("Tap the heart on a track to save it here") }
        } else {
            items(state.liked, key = { "liked-${it.id}" }) { track ->
                LibraryRow(track, isLiked = true, onIntent = onIntent, onOpenAlbum = onOpenAlbum, onOpenArtist = onOpenArtist)
            }
        }

        item { SectionHeader("Recently played") }
        if (state.recent.isEmpty()) {
            item { EmptyHint("Play something to see it here") }
        } else {
            items(state.recent, key = { "recent-${it.id}" }) { track ->
                LibraryRow(track, isLiked = state.isLiked(track.id), onIntent = onIntent, onOpenAlbum = onOpenAlbum, onOpenArtist = onOpenArtist)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = AppSpacing.md, bottom = AppSpacing.sm),
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = AppSpacing.sm))
}

@Composable
private fun LibraryRow(
    track: Track,
    isLiked: Boolean,
    onIntent: (LibraryIntent) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    onOpenArtist: (Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onIntent(LibraryIntent.TrackClicked(track)) }
            .padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppAsyncImage(
            url = track.coverUrl,
            contentDescription = track.title,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(track.albumId?.let { id -> Modifier.clickable { onOpenAlbum(id) } } ?: Modifier),
        )
        Column(Modifier.weight(1f)) {
            Text(
                track.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                track.artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = track.artistId?.let { id -> Modifier.clickable { onOpenArtist(id) } } ?: Modifier,
            )
        }
        Box(
            modifier = Modifier.size(40.dp).clickable { onIntent(LibraryIntent.ToggleLike(track)) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (isLiked) "♥" else "♡",
                color = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
