package com.sangtq.musicappkmp.feature.library

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import com.sangtq.musicappkmp.feature.library.domain.repository.PlaylistRepository
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistNameUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistTracksUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.RemoveTrackFromPlaylistUseCase
import com.sangtq.musicappkmp.feature.library.presentation.UserPlaylistDetailIntent
import com.sangtq.musicappkmp.feature.library.presentation.UserPlaylistDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UserPlaylistDetailViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun track(id: Long) = Track(id, "T$id", "Artist", null, null, null, null, "p", 30, false)

    private fun viewModel(repo: PlaylistRepository) = UserPlaylistDetailViewModel(
        playlistId = 5,
        observeName = ObservePlaylistNameUseCase(repo),
        observeTracks = ObservePlaylistTracksUseCase(repo),
        removeTrack = RemoveTrackFromPlaylistUseCase(repo),
    )

    @Test
    fun exposesNameAndTracks() {
        val state = viewModel(FakeRepo(name = "Chill", tracks = listOf(track(1), track(2)))).state.value
        assertEquals("Chill", state.name)
        assertEquals(listOf(1L, 2L), state.tracks.map { it.id })
    }

    @Test
    fun removeTrackIntent_delegatesToRepository() {
        val repo = FakeRepo(name = "Chill", tracks = listOf(track(1)))
        viewModel(repo).onIntent(UserPlaylistDetailIntent.RemoveTrack(1))
        assertEquals(listOf(5L to 1L), repo.removed)
    }
}

private class FakeRepo(
    private val name: String,
    private val tracks: List<Track>,
) : PlaylistRepository {
    val removed = mutableListOf<Pair<Long, Long>>()

    override val playlists: Flow<List<UserPlaylist>> = flowOf(emptyList())
    override suspend fun create(name: String): Long = 0
    override suspend fun delete(id: Long) = Unit
    override suspend fun addTrack(playlistId: Long, track: Track) = Unit
    override suspend fun removeTrack(playlistId: Long, trackId: Long) {
        removed += playlistId to trackId
    }
    override fun observeName(id: Long): Flow<String?> = flowOf(name)
    override fun observeTracks(id: Long): Flow<List<Track>> = flowOf(tracks)
}
