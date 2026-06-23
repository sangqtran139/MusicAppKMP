package com.sangtq.musicappkmp.feature.search.presentation

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.component.AppAsyncImage
import com.sangtq.musicappkmp.core.designsystem.component.ErrorState
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SearchContent(
    state: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    onOpenArtist: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // Phát hiện gần cuối danh sách → LoadMore (phân trang).
    LaunchedEffect(listState, state.nextIndex) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= state.results.size - 4
        }.distinctUntilChanged().collect { nearEnd ->
            if (nearEnd && state.nextIndex != null) onIntent(SearchIntent.LoadMore)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = AppSpacing.md)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.sm),
            placeholder = { Text("Search tracks, artists…") },
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> ErrorState(
                    message = state.error,
                    onRetry = { onIntent(SearchIntent.Retry) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.results.isEmpty() && state.query.isNotBlank() ->
                    Text("No results", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)

                state.results.isEmpty() ->
                    Text("Type to search", Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurfaceVariant)

                else -> LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(state.results, key = { it.id }) { track ->
                        TrackRow(
                            track = track,
                            onClick = { onIntent(SearchIntent.TrackClicked(track)) },
                            onCoverClick = track.albumId?.let { id -> { onOpenAlbum(id) } },
                            onArtistClick = track.artistId?.let { id -> { onOpenArtist(id) } },
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(AppSpacing.md), Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    onClick: () -> Unit,
    onCoverClick: (() -> Unit)? = null,
    onArtistClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppAsyncImage(
            url = track.coverUrl,
            contentDescription = track.title,
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(if (onCoverClick != null) Modifier.clickable(onClick = onCoverClick) else Modifier),
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
