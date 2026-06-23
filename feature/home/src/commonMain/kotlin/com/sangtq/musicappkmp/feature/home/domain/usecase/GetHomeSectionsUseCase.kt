package com.sangtq.musicappkmp.feature.home.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.feature.home.domain.model.HomeSection
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Dựng các section của Home từ nhiều truy vấn tuyển chọn (Deezer proxy không có /chart,/genre —
 * xem docs/Roadmap.md). Chạy song song; bỏ qua section lỗi/rỗng.
 */
class GetHomeSectionsUseCase(private val repository: CatalogRepository) {

    suspend operator fun invoke(): AppResult<List<HomeSection>> = coroutineScope {
        val sections = SEEDS
            .map { (title, query) -> async { title to repository.search(query, index = 0) } }
            .awaitAll()
            .mapNotNull { (title, result) ->
                (result as? AppResult.Success)?.data?.items
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { HomeSection(title, it) }
            }
        if (sections.isEmpty()) AppResult.Failure(AppError.Network("Không tải được nội dung"))
        else AppResult.Success(sections)
    }

    private companion object {
        val SEEDS = listOf(
            "Top Hits" to "top hits",
            "Pop" to "pop",
            "Lo-Fi Beats" to "lofi",
            "Rock" to "rock",
            "Chill" to "chill",
        )
    }
}
