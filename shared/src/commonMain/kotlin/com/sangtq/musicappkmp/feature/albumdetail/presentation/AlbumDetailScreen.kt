package com.sangtq.musicappkmp.feature.albumdetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sangtq.musicappkmp.catalog.domain.model.Track
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AlbumDetailScreen(
    albumId: Long,
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = koinViewModel { parametersOf(albumId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AlbumDetailEffect.OpenPlayer -> onTrackSelected(effect.track)
            }
        }
    }

    AlbumDetailContent(state = state, onIntent = viewModel::onIntent, onBack = onBack, modifier = modifier)
}
