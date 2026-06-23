package com.sangtq.musicappkmp.feature.library.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.component.AppAsyncImage
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist

@Composable
fun LibraryContent(
    state: LibraryUiState,
    onIntent: (LibraryIntent) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    onOpenArtist: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().safeContentPadding(),
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

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("Playlists (${state.playlists.size})", Modifier.weight(1f))
                Text(
                    "+ New",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onIntent(LibraryIntent.NewPlaylistClicked) }.padding(AppSpacing.sm),
                )
            }
        }
        if (state.playlists.isEmpty()) {
            item { EmptyHint("Tap + New to create your first playlist") }
        } else {
            items(state.playlists, key = { "pl-${it.id}" }) { playlist ->
                PlaylistRow(playlist, onClick = { onIntent(LibraryIntent.PlaylistClicked(playlist.id)) })
            }
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

    if (state.showCreateDialog) {
        CreatePlaylistDialog(
            onCreate = { onIntent(LibraryIntent.CreatePlaylist(it)) },
            onDismiss = { onIntent(LibraryIntent.DismissDialog) },
        )
    }
    state.addTarget?.let { target ->
        AddToPlaylistDialog(
            trackTitle = target.title,
            playlists = state.playlists,
            onPick = { onIntent(LibraryIntent.AddToPlaylist(it)) },
            onDismiss = { onIntent(LibraryIntent.DismissDialog) },
        )
    }
}

@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.padding(top = AppSpacing.md, bottom = AppSpacing.sm),
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = AppSpacing.sm))
}

@Composable
private fun PlaylistRow(playlist: UserPlaylist, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppAsyncImage(
            url = playlist.coverUrl,
            contentDescription = playlist.name,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Column(Modifier.weight(1f)) {
            Text(
                playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "${playlist.trackCount} songs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
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
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
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
        IconText("＋", "Add to playlist") { onIntent(LibraryIntent.AddToPlaylistClicked(track)) }
        IconText(
            if (isLiked) "♥" else "♡",
            "Like",
            tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        ) { onIntent(LibraryIntent.ToggleLike(track)) }
    }
}

@Composable
private fun IconText(
    symbol: String,
    contentDescription: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.size(40.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(symbol, color = tint, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
internal fun CreatePlaylistDialog(onCreate: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New playlist") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("Playlist name") },
            )
        },
        confirmButton = { TextButton(onClick = { onCreate(name) }) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
internal fun AddToPlaylistDialog(
    trackTitle: String,
    playlists: List<UserPlaylist>,
    onPick: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to playlist") },
        text = {
            if (playlists.isEmpty()) {
                Text("No playlists yet — create one with + New.")
            } else {
                Column {
                    Text(
                        trackTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = AppSpacing.sm),
                    )
                    playlists.forEach { pl ->
                        Text(
                            pl.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth().clickable { onPick(pl.id) }.padding(vertical = AppSpacing.sm),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}
