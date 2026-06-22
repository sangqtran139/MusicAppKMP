package com.sangtq.musicappkmp.feature.playlistdetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sangtq.musicappkmp.catalog.domain.model.Track
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    onOpenArtist: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailViewModel = koinViewModel { parametersOf(playlistId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PlaylistDetailEffect.OpenPlayer -> onTrackSelected(effect.track)
            }
        }
    }

    PlaylistDetailContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onOpenArtist = onOpenArtist,
        modifier = modifier,
    )
}
