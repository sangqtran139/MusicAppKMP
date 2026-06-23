package com.sangtq.musicappkmp.feature.albumdetail

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.catalog.domain.usecase.GetAlbumUseCase
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.feature.albumdetail.presentation.AlbumDetailIntent
import com.sangtq.musicappkmp.feature.albumdetail.presentation.AlbumDetailViewModel
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val album = Album(
        id = 1,
        title = "The Eminem Show",
        artistName = "Eminem",
        coverUrl = "cover",
        releaseDate = "2002-05-26",
        trackCount = 1,
        tracks = listOf(
            Track(1, "Without Me", "Eminem", 13, "The Eminem Show", 1, "cover", "preview", 290, true),
        ),
    )

    private fun viewModel(flow: Flow<Resource<Album>>): AlbumDetailViewModel =
        AlbumDetailViewModel(albumId = 1, getAlbum = GetAlbumUseCase(FakeCatalogRepository(flow)))

    @Test
    fun success_setsAlbumAndClearsLoading() {
        val vm = viewModel(flowOf(Resource.Success(album)))
        val state = vm.state.value
        assertEquals(album, state.album)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun loading_setsIsLoading() {
        val vm = viewModel(MutableStateFlow(Resource.Loading(null)))
        assertTrue(vm.state.value.isLoading)
        assertNull(vm.state.value.album)
    }

    @Test
    fun error_withoutCache_setsErrorMessage() {
        val vm = viewModel(flowOf(Resource.Error(AppError.Network("offline"), null)))
        assertEquals("offline", vm.state.value.error)
        assertNull(vm.state.value.album)
    }

    @Test
    fun error_withCache_keepsCachedAlbum() {
        val vm = viewModel(flowOf(Resource.Error(AppError.Network("offline"), album)))
        assertEquals(album, vm.state.value.album)
        assertEquals("offline", vm.state.value.error)
    }

    @Test
    fun retry_reobservesSource() {
        val repo = FakeCatalogRepository(flowOf(Resource.Success(album)))
        val vm = AlbumDetailViewModel(albumId = 1, getAlbum = GetAlbumUseCase(repo))
        assertEquals(1, repo.observeAlbumCount)

        vm.onIntent(AlbumDetailIntent.Retry)
        assertEquals(2, repo.observeAlbumCount)
    }
}

private class FakeCatalogRepository(private val albumFlow: Flow<Resource<Album>>) : CatalogRepository {
    var observeAlbumCount = 0
        private set

    override fun observeAlbum(id: Long): Flow<Resource<Album>> {
        observeAlbumCount++
        return albumFlow
    }

    override fun observeArtist(id: Long): Flow<Resource<Artist>> = flowOf()
    override fun observePlaylist(id: Long): Flow<Resource<Playlist>> = flowOf()

    override suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchPage> =
        AppResult.Failure(AppError.Unknown("not used"))

    override suspend fun getTrack(id: Long): AppResult<Track> =
        AppResult.Failure(AppError.Unknown("not used"))
}
