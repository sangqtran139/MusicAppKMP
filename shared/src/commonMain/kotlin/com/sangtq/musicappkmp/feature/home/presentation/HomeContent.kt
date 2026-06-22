package com.sangtq.musicappkmp.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.designsystem.component.AppAsyncImage
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing
import com.sangtq.musicappkmp.feature.home.domain.model.HomeSection

@Composable
fun HomeContent(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading && state.sections.isEmpty() ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            state.error != null && state.sections.isEmpty() -> Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Text(state.error, color = MaterialTheme.colorScheme.error)
                Text(
                    "Tap to retry",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onIntent(HomeIntent.Retry) }.padding(AppSpacing.sm),
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().safeContentPadding(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = AppSpacing.md),
            ) {
                item {
                    Text(
                        "Welcome back",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
                    )
                }
                items(state.sections, key = { it.title }) { section ->
                    SectionRow(
                        section = section,
                        onClick = { onIntent(HomeIntent.TrackClicked(it)) },
                        onOpenAlbum = onOpenAlbum,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionRow(section: HomeSection, onClick: (Track) -> Unit, onOpenAlbum: (Long) -> Unit) {
    Column(Modifier.padding(vertical = AppSpacing.sm)) {
        Text(
            section.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        )
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = AppSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            items(section.items, key = { it.id }) { track ->
                TrackCard(
                    track = track,
                    onClick = { onClick(track) },
                    onCoverClick = track.albumId?.let { id -> { onOpenAlbum(id) } },
                )
            }
        }
    }
}

@Composable
private fun TrackCard(track: Track, onClick: () -> Unit, onCoverClick: (() -> Unit)? = null) {
    Column(modifier = Modifier.width(140.dp).clickable(onClick = onClick)) {
        AppAsyncImage(
            url = track.coverUrl,
            contentDescription = track.title,
            modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(if (onCoverClick != null) Modifier.clickable(onClick = onCoverClick) else Modifier),
        )
        Text(
            track.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = AppSpacing.xs).fillMaxWidth(),
        )
        Text(
            track.artistName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
