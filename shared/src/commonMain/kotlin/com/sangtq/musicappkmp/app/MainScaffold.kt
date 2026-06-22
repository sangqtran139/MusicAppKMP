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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.albumdetail.presentation.AlbumDetailScreen
import com.sangtq.musicappkmp.feature.artistdetail.presentation.ArtistDetailScreen
import com.sangtq.musicappkmp.feature.home.presentation.HomeScreen
import com.sangtq.musicappkmp.feature.playlistdetail.presentation.PlaylistDetailScreen
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddRecentUseCase
import com.sangtq.musicappkmp.feature.library.presentation.LibraryScreen
import com.sangtq.musicappkmp.feature.player.presentation.MiniPlayer
import com.sangtq.musicappkmp.feature.player.presentation.PlayerContent
import com.sangtq.musicappkmp.feature.player.presentation.PlayerIntent
import com.sangtq.musicappkmp.feature.player.presentation.PlayerViewModel
import com.sangtq.musicappkmp.feature.player.presentation.toPlayable
import com.sangtq.musicappkmp.feature.search.presentation.SearchScreen
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private data class RootTab(val label: String, val route: Any, val routeClass: KClass<*>)

private val rootTabs = listOf(
    RootTab("Home", HomeRoute, HomeRoute::class),
    RootTab("Explore", ExploreRoute, ExploreRoute::class),
    RootTab("Library", LibraryRoute, LibraryRoute::class),
)

/**
 * Khung chính: NavHost (3 tab + Album detail) + mini-player + now-playing overlay (ADR-0008).
 * PlayerViewModel hoist tại đây để mini-player và now-playing dùng chung một PlaybackState.
 */
@Composable
fun MainScaffold() {
    val playerVm: PlayerViewModel = koinViewModel()
    val addRecent: AddRecentUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val playerState by playerVm.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    var showNowPlaying by remember { mutableStateOf(false) }

    fun playSelected(track: Track) {
        track.toPlayable()?.let {
            scope.launch { addRecent(track) }
            playerVm.onIntent(PlayerIntent.Play(it))
            showNowPlaying = true
        }
    }

    fun openAlbum(albumId: Long) = navController.navigate(AlbumRoute(albumId))
    fun openArtist(artistId: Long) = navController.navigate(ArtistRoute(artistId))
    fun openPlaylist(playlistId: Long) = navController.navigate(PlaylistRoute(playlistId))

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
                    val currentEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = currentEntry?.destination
                    NavigationBar {
                        rootTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.hasRoute(tab.routeClass) } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Text(tab.label.first().toString()) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                composable<HomeRoute> {
                    HomeScreen(
                        onTrackSelected = ::playSelected,
                        onOpenAlbum = ::openAlbum,
                        onOpenPlaylist = ::openPlaylist,
                    )
                }
                composable<ExploreRoute> {
                    SearchScreen(
                        onTrackSelected = ::playSelected,
                        onOpenAlbum = ::openAlbum,
                        onOpenArtist = ::openArtist,
                    )
                }
                composable<LibraryRoute> {
                    LibraryScreen(
                        onTrackSelected = ::playSelected,
                        onOpenAlbum = ::openAlbum,
                        onOpenArtist = ::openArtist,
                    )
                }
                composable<AlbumRoute> { entry ->
                    AlbumDetailScreen(
                        albumId = entry.toRoute<AlbumRoute>().albumId,
                        onBack = { navController.popBackStack() },
                        onTrackSelected = ::playSelected,
                        onOpenArtist = ::openArtist,
                    )
                }
                composable<ArtistRoute> { entry ->
                    ArtistDetailScreen(
                        artistId = entry.toRoute<ArtistRoute>().artistId,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable<PlaylistRoute> { entry ->
                    PlaylistDetailScreen(
                        playlistId = entry.toRoute<PlaylistRoute>().playlistId,
                        onBack = { navController.popBackStack() },
                        onTrackSelected = ::playSelected,
                        onOpenArtist = ::openArtist,
                    )
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
