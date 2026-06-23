package com.sangtq.musicappkmp.catalog.domain.model

/** Một trang kết quả tìm kiếm. `nextIndex` != null nếu còn trang sau (phân trang index/limit). */
data class SearchPage(
    val items: List<Track>,
    val total: Int,
    val nextIndex: Int?,
)
