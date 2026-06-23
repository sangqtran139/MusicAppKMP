package com.sangtq.musicappkmp.feature.artistdetail

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.catalog.domain.usecase.GetArtistUseCase
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.feature.artistdetail.presentation.ArtistDetailViewModel
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistDetailViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val artist = Artist(13, "Eminem", "pic", 12, 999)

    private fun viewModel(flow: Flow<Resource<Artist>>) =
        ArtistDetailViewModel(artistId = 13, getArtist = GetArtistUseCase(FakeRepo(flow)))

    @Test
    fun success_setsArtist() {
        val state = viewModel(flowOf(Resource.Success(artist))).state.value
        assertEquals(artist, state.artist)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun error_setsMessage() {
        val state = viewModel(flowOf(Resource.Error(AppError.Network("offline"), null))).state.value
        assertEquals("offline", state.error)
        assertNull(state.artist)
    }
}

private class FakeRepo(private val artistFlow: Flow<Resource<Artist>>) : CatalogRepository {
    override fun observeArtist(id: Long): Flow<Resource<Artist>> = artistFlow
    override fun observeAlbum(id: Long): Flow<Resource<Album>> = flowOf()
    override fun observePlaylist(id: Long): Flow<Resource<Playlist>> = flowOf()
    override suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchPage> =
        AppResult.Failure(AppError.Unknown("nu"))
    override suspend fun getTrack(id: Long): AppResult<Track> = AppResult.Failure(AppError.Unknown("nu"))
}
