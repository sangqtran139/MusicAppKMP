package com.sangtq.musicappkmp.feature.player.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.playback.PlayableTrack

/** Map catalog Track → PlayableTrack. Trả null nếu không có preview để phát. */
fun Track.toPlayable(): PlayableTrack? =
    previewUrl?.let { PlayableTrack(id = id, title = title, artist = artistName, artworkUrl = coverUrl, url = it) }
