package com.sangtq.musicappkmp.feature.library

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveLikedUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ToggleLikeUseCase
import com.sangtq.musicappkmp.feature.library.presentation.LibraryIntent
import com.sangtq.musicappkmp.feature.library.presentation.LibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private fun viewModel(repo: LibraryRepository) = LibraryViewModel(
        observeLiked = ObserveLikedUseCase(repo),
        observeRecent = ObserveRecentUseCase(repo),
        toggleLike = ToggleLikeUseCase(repo),
    )

    @Test
    fun exposesLikedAndRecentFromRepository() {
        val repo = FakeLibraryRepository(liked = listOf(track(1)), recent = listOf(track(2)))
        val state = viewModel(repo).state.value
        assertEquals(listOf(1L), state.liked.map { it.id })
        assertEquals(listOf(2L), state.recent.map { it.id })
    }

    @Test
    fun toggleLikeIntent_delegatesToRepository() {
        val repo = FakeLibraryRepository()
        val vm = viewModel(repo)
        vm.onIntent(LibraryIntent.ToggleLike(track(1)))
        assertEquals(1, repo.toggleCount)
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
