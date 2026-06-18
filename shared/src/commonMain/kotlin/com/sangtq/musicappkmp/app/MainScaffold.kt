package com.sangtq.musicappkmp.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.home.presentation.HomeScreen
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddRecentUseCase
import com.sangtq.musicappkmp.feature.library.presentation.LibraryScreen
import com.sangtq.musicappkmp.feature.player.presentation.MiniPlayer
import com.sangtq.musicappkmp.feature.player.presentation.PlayerContent
import com.sangtq.musicappkmp.feature.player.presentation.PlayerIntent
import com.sangtq.musicappkmp.feature.player.presentation.PlayerViewModel
import com.sangtq.musicappkmp.feature.player.presentation.toPlayable
import com.sangtq.musicappkmp.feature.search.presentation.SearchScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private enum class RootTab(val label: String) {
    Home("Home"),
    Explore("Explore"),
    Library("Library"),
}

/**
 * Khung chính: bottom nav 3 tab + mini-player + now-playing overlay. PlayerViewModel hoist tại
 * đây để mini-player và now-playing dùng chung một PlaybackState.
 */
@Composable
fun MainScaffold() {
    val playerVm: PlayerViewModel = koinViewModel()
    val addRecent: AddRecentUseCase = koinInject()
    val playerState by playerVm.state.collectAsStateWithLifecycle()

    var selected by remember { mutableStateOf(RootTab.Home) }
    var showNowPlaying by remember { mutableStateOf(false) }

    fun playSelected(track: Track) {
        track.toPlayable()?.let {
            addRecent(track)
            playerVm.onIntent(PlayerIntent.Play(it))
            showNowPlaying = true
        }
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column {
                    if (playerState.track != null) {
                        MiniPlayer(
                            state = playerState,
                            onExpand = { showNowPlaying = true },
                            onTogglePlayPause = { playerVm.onIntent(PlayerIntent.TogglePlayPause) },
                        )
                    }
                    NavigationBar {
                        RootTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selected == tab,
                                onClick = { selected = tab },
                                icon = { Text(tab.label.first().toString()) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (selected) {
                    RootTab.Home -> HomeScreen(onTrackSelected = ::playSelected)
                    RootTab.Explore -> SearchScreen(onTrackSelected = ::playSelected)
                    RootTab.Library -> LibraryScreen(onTrackSelected = ::playSelected)
                }
            }
        }

        if (showNowPlaying && playerState.track != null) {
            PlayerContent(
                state = playerState,
                onBack = { showNowPlaying = false },
                onTogglePlayPause = { playerVm.onIntent(PlayerIntent.TogglePlayPause) },
                onSeek = { playerVm.onIntent(PlayerIntent.SeekTo(it)) },
            )
        }
    }
}
