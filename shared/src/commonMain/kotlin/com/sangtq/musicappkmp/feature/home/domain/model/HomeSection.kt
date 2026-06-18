package com.sangtq.musicappkmp.feature.home.domain.model

import com.sangtq.musicappkmp.catalog.domain.model.Track

/** Một hàng nội dung trên Home (tuyển chọn theo chủ đề). */
data class HomeSection(
    val title: String,
    val items: List<Track>,
)
