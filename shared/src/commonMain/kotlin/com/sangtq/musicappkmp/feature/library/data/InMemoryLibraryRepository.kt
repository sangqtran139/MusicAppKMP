package com.sangtq.musicappkmp.feature.library.data

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryLibraryRepository : LibraryRepository {

    private val _liked = MutableStateFlow<List<Track>>(emptyList())
    override val liked: StateFlow<List<Track>> = _liked.asStateFlow()

    private val _recent = MutableStateFlow<List<Track>>(emptyList())
    override val recent: StateFlow<List<Track>> = _recent.asStateFlow()

    override fun toggleLike(track: Track) = _liked.update { current ->
        if (current.any { it.id == track.id }) current.filterNot { it.id == track.id }
        else listOf(track) + current
    }

    override fun addRecent(track: Track) = _recent.update { current ->
        (listOf(track) + current.filterNot { it.id == track.id }).take(MAX_RECENT)
    }

    private companion object {
        const val MAX_RECENT = 20
    }
}
