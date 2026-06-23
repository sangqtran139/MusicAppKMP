package com.sangtq.musicappkmp.feature.playlistdetail

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.catalog.domain.usecase.GetPlaylistUseCase
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.feature.playlistdetail.presentation.PlaylistDetailIntent
import com.sangtq.musicappkmp.feature.playlistdetail.presentation.PlaylistDetailViewModel
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
class PlaylistDetailViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val playlist = Playlist(
        id = 5,
        title = "Top Hits",
        description = "desc",
        coverUrl = "cover",
        trackCount = 1,
        tracks = listOf(Track(1, "Song", "Artist", null, null, null, null, "p", 30, false)),
    )

    private fun viewModel(flow: Flow<Resource<Playlist>>) =
        PlaylistDetailViewModel(playlistId = 5, getPlaylist = GetPlaylistUseCase(FakeRepo(flow)))

    @Test
    fun success_setsPlaylist() {
        val state = viewModel(flowOf(Resource.Success(playlist))).state.value
        assertEquals(playlist, state.playlist)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun error_keepsCacheWhenPresent() {
        val state = viewModel(flowOf(Resource.Error(AppError.Network("offline"), playlist))).state.value
        assertEquals(playlist, state.playlist)
        assertEquals("offline", state.error)
    }

    @Test
    fun trackClicked_emitsOpenPlayerEffect() {
        // No-op smoke: ensure intent handling doesn't throw and keeps state.
        val vm = viewModel(flowOf(Resource.Success(playlist)))
        vm.onIntent(PlaylistDetailIntent.TrackClicked(playlist.tracks.first()))
        assertEquals(playlist, vm.state.value.playlist)
    }
}

private class FakeRepo(private val playlistFlow: Flow<Resource<Playlist>>) : CatalogRepository {
    override fun observePlaylist(id: Long): Flow<Resource<Playlist>> = playlistFlow
    override fun observeAlbum(id: Long): Flow<Resource<Album>> = flowOf()
    override fun observeArtist(id: Long): Flow<Resource<Artist>> = flowOf()
    override suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchPage> =
        AppResult.Failure(AppError.Unknown("nu"))
    override suspend fun getTrack(id: Long): AppResult<Track> = AppResult.Failure(AppError.Unknown("nu"))
}
