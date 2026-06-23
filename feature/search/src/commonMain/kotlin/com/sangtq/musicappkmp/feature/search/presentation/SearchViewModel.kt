package com.sangtq.musicappkmp.feature.search.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.SearchCatalogUseCase
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchCatalog: SearchCatalogUseCase,
) : MviViewModel<SearchUiState, SearchIntent, SearchEffect>(SearchUiState()) {

    private val queryFlow = MutableStateFlow("")
    private var activeQuery = ""

    init {
        queryFlow
            .debounce(DEBOUNCE_MS)
            .map { it.trim() }
            .distinctUntilChanged()
            .onEach { q -> if (q.isBlank()) clearResults() else runSearch(q, index = 0) }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: SearchIntent) = when (intent) {
        is SearchIntent.QueryChanged -> {
            setState { copy(query = intent.value) }
            queryFlow.value = intent.value
        }
        SearchIntent.LoadMore -> loadMore()
        SearchIntent.Retry -> activeQuery.takeIf { it.isNotBlank() }?.let { runSearch(it, 0) } ?: Unit
        is SearchIntent.TrackClicked -> sendEffect(SearchEffect.OpenPlayer(intent.track))
    }

    private fun clearResults() = setState {
        copy(isLoading = false, isLoadingMore = false, results = emptyList(), total = 0, nextIndex = null, error = null)
    }

    private fun runSearch(query: String, index: Int) {
        activeQuery = query
        val loadMore = index > 0
        setState { copy(isLoading = !loadMore, isLoadingMore = loadMore, error = null) }
        viewModelScope.launch {
            when (val result = searchCatalog(query, index)) {
                is AppResult.Success -> setState {
                    val page = result.data
                    copy(
                        isLoading = false,
                        isLoadingMore = false,
                        results = if (loadMore) results + page.items else page.items,
                        total = page.total,
                        nextIndex = page.nextIndex,
                    )
                }
                is AppResult.Failure -> setState {
                    copy(isLoading = false, isLoadingMore = false, error = result.error.message)
                }
            }
        }
    }

    private fun loadMore() {
        val next = currentState.nextIndex ?: return
        if (currentState.isLoading || currentState.isLoadingMore) return
        runSearch(activeQuery, next)
    }

    private companion object {
        const val DEBOUNCE_MS = 300L
    }
}
