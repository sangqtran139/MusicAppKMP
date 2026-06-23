package com.sangtq.musicappkmp.feature.search

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.catalog.domain.usecase.SearchCatalogUseCase
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.feature.search.presentation.SearchIntent
import com.sangtq.musicappkmp.feature.search.presentation.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private fun track(id: Long) = Track(id, "T$id", "Artist", null, null, null, null, "p", 30, false)
    private fun page(vararg ids: Long) = SearchPage(ids.map { track(it) }, ids.size, null)

    @Test
    fun debouncedQuery_populatesResults() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakeSearchRepository(AppResult.Success(page(1, 2)))
            val vm = SearchViewModel(SearchCatalogUseCase(repo))

            vm.onIntent(SearchIntent.QueryChanged("eminem"))
            advanceTimeBy(301)
            runCurrent()

            assertEquals(listOf(1L, 2L), vm.state.value.results.map { it.id })
            assertEquals(1, repo.searchCount)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun rapidInput_debouncesToSingleSearch() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakeSearchRepository(AppResult.Success(page(1)))
            val vm = SearchViewModel(SearchCatalogUseCase(repo))

            vm.onIntent(SearchIntent.QueryChanged("a"))
            advanceTimeBy(100)
            vm.onIntent(SearchIntent.QueryChanged("ab"))
            advanceTimeBy(100)
            vm.onIntent(SearchIntent.QueryChanged("abc"))
            advanceTimeBy(301)
            runCurrent()

            assertEquals(1, repo.searchCount)
            assertEquals("abc", repo.lastQuery)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun blankQuery_clearsWithoutSearching() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repo = FakeSearchRepository(AppResult.Success(page(1)))
            val vm = SearchViewModel(SearchCatalogUseCase(repo))

            vm.onIntent(SearchIntent.QueryChanged("   "))
            advanceTimeBy(301)
            runCurrent()

            assertEquals(0, repo.searchCount)
            assertTrue(vm.state.value.results.isEmpty())
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class FakeSearchRepository(private val result: AppResult<SearchPage>) : CatalogRepository {
    var searchCount = 0
        private set
    var lastQuery: String? = null
        private set

    override suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchPage> {
        searchCount++
        lastQuery = query
        return result
    }

    override suspend fun getTrack(id: Long): AppResult<Track> = AppResult.Failure(AppError.Unknown("nu"))
    override fun observeAlbum(id: Long): Flow<Resource<Album>> = flowOf()
    override fun observeArtist(id: Long): Flow<Resource<Artist>> = flowOf()
    override fun observePlaylist(id: Long): Flow<Resource<Playlist>> = flowOf()
}
