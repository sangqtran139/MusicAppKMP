package com.sangtq.musicappkmp.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sangtq.musicappkmp.catalog.domain.model.Track
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onTrackSelected: (Track) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.OpenPlayer -> onTrackSelected(effect.track)
            }
        }
    }

    HomeContent(state = state, onIntent = viewModel::onIntent, onOpenAlbum = onOpenAlbum, modifier = modifier)
}
