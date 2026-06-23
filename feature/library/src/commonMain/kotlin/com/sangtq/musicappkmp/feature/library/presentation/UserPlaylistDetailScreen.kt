package com.sangtq.musicappkmp.feature.library.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sangtq.musicappkmp.catalog.domain.model.Track
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserPlaylistDetailScreen(
    playlistId: Long,
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserPlaylistDetailViewModel = koinViewModel { parametersOf(playlistId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is UserPlaylistDetailEffect.OpenPlayer -> onTrackSelected(effect.track)
            }
        }
    }

    UserPlaylistDetailContent(state = state, onIntent = viewModel::onIntent, onBack = onBack, modifier = modifier)
}
