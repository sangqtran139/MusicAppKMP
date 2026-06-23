package com.sangtq.musicappkmp.feature.home

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.feature.home.domain.usecase.GetFeaturedPlaylistsUseCase
import com.sangtq.musicappkmp.feature.home.domain.usecase.GetHomeSectionsUseCase
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.feature.home.presentation.HomeViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun track(id: Long) = Track(id, "T$id", "Artist", null, null, null, null, "p", 30, false)
    private fun playlist(id: Long) = Playlist(id, "PL$id", null, "cover", 1, listOf(track(id)))

    private fun viewModel(repo: CatalogRepository) =
        HomeViewModel(GetHomeSectionsUseCase(repo), GetFeaturedPlaylistsUseCase(repo))

    @Test
    fun success_populatesSectionsAndPlaylists() {
        val vm = viewModel(FakeCatalogRepository(searchResult = AppResult.Success(SearchPage(listOf(track(1)), 1, null))))
        val state = vm.state.value
        assertEquals(false, state.isLoading)
        assertNull(state.error)
        assertTrue(state.sections.isNotEmpty())
        assertTrue(state.playlists.isNotEmpty())
    }

    @Test
    fun allSectionsFail_setsError() {
        val vm = viewModel(FakeCatalogRepository(searchResult = AppResult.Failure(AppError.Network("down"))))
        val state = vm.state.value
        assertEquals(false, state.isLoading)
        assertEquals(true, state.sections.isEmpty())
        assertEquals("Không tải được nội dung", state.error)
    }
}

private class FakeCatalogRepository(
    private val searchResult: AppResult<SearchPage>,
) : CatalogRepository {
    override suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchPage> = searchResult
    override suspend fun getTrack(id: Long): AppResult<Track> = AppResult.Failure(AppError.Unknown("nu"))
    override fun observeAlbum(id: Long): Flow<Resource<Album>> = flowOf()
    override fun observeArtist(id: Long): Flow<Resource<Artist>> = flowOf()
    override fun observePlaylist(id: Long): Flow<Resource<Playlist>> =
        flowOf(Resource.Success(Playlist(id, "PL$id", null, "cover", 1, emptyList())))
}
