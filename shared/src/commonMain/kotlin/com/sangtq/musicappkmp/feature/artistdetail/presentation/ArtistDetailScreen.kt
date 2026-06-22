package com.sangtq.musicappkmp.feature.artistdetail.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ArtistDetailScreen(
    artistId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailViewModel = koinViewModel { parametersOf(artistId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ArtistDetailContent(state = state, onIntent = viewModel::onIntent, onBack = onBack, modifier = modifier)
}
