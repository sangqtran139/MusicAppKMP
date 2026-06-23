package com.sangtq.musicappkmp.core.playback

/** Model tối thiểu để phát (độc lập với catalog domain). `url` = preview mp3 ~30s của Deezer. */
data class PlayableTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUrl: String?,
    val url: String,
)
