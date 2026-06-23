package com.sangtq.musicappkmp.feature.library

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import com.sangtq.musicappkmp.feature.library.domain.repository.PlaylistRepository
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddTrackToPlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.CreatePlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveLikedUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistsUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ToggleLikeUseCase
import com.sangtq.musicappkmp.feature.library.presentation.LibraryIntent
import com.sangtq.musicappkmp.feature.library.presentation.LibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun track(id: Long) = Track(id, "T$id", "Artist", null, null, null, null, "p", 30, false)

    private fun viewModel(
        libRepo: LibraryRepository,
        plRepo: PlaylistRepository,
    ) = LibraryViewModel(
        observeLiked = ObserveLikedUseCase(libRepo),
        observeRecent = ObserveRecentUseCase(libRepo),
        observePlaylists = ObservePlaylistsUseCase(plRepo),
        toggleLike = ToggleLikeUseCase(libRepo),
        createPlaylist = CreatePlaylistUseCase(plRepo),
        addToPlaylist = AddTrackToPlaylistUseCase(plRepo),
    )

    @Test
    fun exposesLikedRecentAndPlaylists() {
        val lib = FakeLibraryRepository(liked = listOf(track(1)), recent = listOf(track(2)))
        val pl = FakePlaylistRepository(initial = listOf(UserPlaylist(1, "Rock", 3, null)))
        val state = viewModel(lib, pl).state.value
        assertEquals(listOf(1L), state.liked.map { it.id })
        assertEquals(listOf(2L), state.recent.map { it.id })
        assertEquals(listOf("Rock"), state.playlists.map { it.name })
    }

    @Test
    fun toggleLikeIntent_delegatesToRepository() {
        val lib = FakeLibraryRepository()
        val vm = viewModel(lib, FakePlaylistRepository())
        vm.onIntent(LibraryIntent.ToggleLike(track(1)))
        assertEquals(1, lib.toggleCount)
    }

    @Test
    fun createPlaylistIntent_delegatesToRepository() {
        val pl = FakePlaylistRepository()
        val vm = viewModel(FakeLibraryRepository(), pl)
        vm.onIntent(LibraryIntent.CreatePlaylist("Chill"))
        assertEquals(listOf("Chill"), pl.created)
    }

    @Test
    fun addToPlaylistIntent_delegatesToRepository() {
        val pl = FakePlaylistRepository()
        val vm = viewModel(FakeLibraryRepository(), pl)
        vm.onIntent(LibraryIntent.AddToPlaylistClicked(track(7)))
        vm.onIntent(LibraryIntent.AddToPlaylist(playlistId = 1))
        assertEquals(listOf(1L to 7L), pl.added)
    }
}

private class FakeLibraryRepository(
    liked: List<Track> = emptyList(),
    recent: List<Track> = emptyList(),
) : LibraryRepository {
    override val liked: Flow<List<Track>> = MutableStateFlow(liked)
    override val recent: Flow<List<Track>> = MutableStateFlow(recent)
    var toggleCount = 0
        private set

    override suspend fun toggleLike(track: Track) {
        toggleCount++
    }

    override suspend fun addRecent(track: Track) = Unit
}

private class FakePlaylistRepository(
    initial: List<UserPlaylist> = emptyList(),
) : PlaylistRepository {
    override val playlists: Flow<List<UserPlaylist>> = MutableStateFlow(initial)
    val created = mutableListOf<String>()
    val added = mutableListOf<Pair<Long, Long>>()

    override suspend fun create(name: String): Long {
        created += name
        return created.size.toLong()
    }

    override suspend fun delete(id: Long) = Unit
    override suspend fun addTrack(playlistId: Long, track: Track) {
        added += playlistId to track.id
    }

    override suspend fun removeTrack(playlistId: Long, trackId: Long) = Unit
    override fun observeName(id: Long): Flow<String?> = flowOf(null)
    override fun observeTracks(id: Long): Flow<List<Track>> = flowOf(emptyList())
}
