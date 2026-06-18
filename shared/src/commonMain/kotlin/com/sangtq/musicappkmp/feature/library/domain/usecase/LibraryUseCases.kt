package com.sangtq.musicappkmp.feature.library.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveLikedUseCase(private val repository: LibraryRepository) {
    operator fun invoke(): StateFlow<List<Track>> = repository.liked
}

class ObserveRecentUseCase(private val repository: LibraryRepository) {
    operator fun invoke(): StateFlow<List<Track>> = repository.recent
}

class ToggleLikeUseCase(private val repository: LibraryRepository) {
    operator fun invoke(track: Track) = repository.toggleLike(track)
}

class AddRecentUseCase(private val repository: LibraryRepository) {
    operator fun invoke(track: Track) = repository.addRecent(track)
}
