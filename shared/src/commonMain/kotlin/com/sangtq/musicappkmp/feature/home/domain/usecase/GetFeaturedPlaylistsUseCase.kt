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
 * [SEED_IDS] là các playlist công khai của Deezer (curate thủ công). Nếu Deezer đổi/ẩn một
 * playlist thì card đó tự ẩn (graceful skip) — cập nhật danh sách khi cần.
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
        // Playlist công khai Deezer (gọi /playlist/{id} qua proxy được — chỉ /chart bị chặn).
        val SEED_IDS = listOf(
            3155776842L, // Top Worldwide
            1313621735L, // Top USA
            1363560485L, // Deezer Hits
            908622995L,  // playlist ví dụ trong docs Deezer API
        )
    }
}
