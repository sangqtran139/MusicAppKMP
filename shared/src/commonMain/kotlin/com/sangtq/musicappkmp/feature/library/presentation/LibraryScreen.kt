package com.sangtq.musicappkmp.feature.library.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sangtq.musicappkmp.catalog.domain.model.Track
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryScreen(
    onTrackSelected: (Track) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LibraryEffect.OpenPlayer -> onTrackSelected(effect.track)
            }
        }
    }

    LibraryContent(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}
