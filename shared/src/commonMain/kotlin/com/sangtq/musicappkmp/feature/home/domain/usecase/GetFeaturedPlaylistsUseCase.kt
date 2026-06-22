package com.sangtq.musicappkmp.feature.home.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.feature.home.domain.model.FeaturedPlaylist
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Tải các playlist tuyển chọn (ID cố định) để hiển thị ở Home — proxy không có /chart,/editorial
 * nên dùng playlist ID seed (xem docs/Api/DeezerApi.md §9). Chạy song song; bỏ qua ID lỗi.
 *
 * ⚠️ [SEED_IDS] là placeholder cần team curate bằng playlist ID Deezer công khai còn sống.
 */
class GetFeaturedPlaylistsUseCase(private val repository: CatalogRepository) {

    suspend operator fun invoke(): List<FeaturedPlaylist> = coroutineScope {
        SEED_IDS
            .map { id -> async { repository.getPlaylist(id) } }
            .awaitAll()
            .mapNotNull { result ->
                (result as? AppResult.Success)?.data?.let {
                    FeaturedPlaylist(id = it.id, title = it.title, coverUrl = it.coverUrl)
                }
            }
    }

    private companion object {
        val SEED_IDS = listOf(
            908622995L,
            1313621735L,
            1652248171L,
            1282495565L,
            3155776842L,
        )
    }
}
