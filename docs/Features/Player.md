# Feature: Player

Module: `:feature:player` — package `com.sangtq.musicappkmp.feature.player`

## Purpose

Điều khiển và hiển thị phát nhạc: màn hình now-playing đầy đủ + mini-player hiện ở đáy app. Là nơi
duy nhất tương tác với `core:playback`.

## Business Flow

- Nhận một track/queue để phát → đẩy vào `AudioPlayer` (qua `core:playback`).
- Quan sát `StateFlow<PlaybackState>` (đang phát, vị trí, buffering) — **một nguồn sự thật cho toàn app**.
- Mini-player và now-playing cùng quan sát state này → luôn đồng bộ.
- Điều khiển: play/pause, seek, next/previous; phát nền + media notification (Android) / Control Center (iOS).

## User Flow

```
(từ Home/Search/Library) tap track ─▶ bắt đầu phát + hiện Mini-player
Mini-player ─▶ tap ─▶ Now-playing (full) ─▶ play/pause/seek/next/prev ─▶ back ─▶ Mini-player
```

## Screens

- `PlayerScreen` (stateful) + `PlayerContent` (stateless): artwork, tên bài/artist, thanh seek, nút điều khiển.
- `MiniPlayer` (stateless component, đặt ở scaffold trong `:shared`): quan sát cùng `PlaybackState`.

## Navigation Flow

- Route: `@Serializable data class PlayerRoute(val trackId: String)`.
- `NavGraphBuilder.playerGraph(onBack: () -> Unit)`. Mini-player không phải destination — là overlay ở `:shared`.

## State Flow

```kotlin
data class PlayerUiState(
    val isLoading: Boolean = false,
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val error: String? = null,
) : UiState

sealed interface PlayerIntent : Intent {
    data object PlayClicked : PlayerIntent
    data object PauseClicked : PlayerIntent
    data class SeekTo(val fraction: Float) : PlayerIntent
    data object Next : PlayerIntent
    data object Previous : PlayerIntent
    data object Retry : PlayerIntent
}

sealed interface PlayerEffect : Effect {
    data object NavigateBack : PlayerEffect
    data class ShowError(val message: String) : PlayerEffect
}
```

ViewModel quan sát `PlaybackController.state` (bọc `AudioPlayer`) và map sang `PlayerUiState`. UseCase:
`PlayTrackUseCase`, `ObservePlaybackStateUseCase`.

## API Used

- Không gọi API trực tiếp để phát; URL stream lấy từ catalog (qua repository) rồi đưa vào `MediaItem`.
- Token-signed URL được resolve ở `data` trước khi tới `core:playback`.

## Related Modules

- `core:playback` (`AudioPlayer` expect/actual — ExoPlayer/AVPlayer, [ADR-0010](../ADR/0010-audio-playback-expect-actual.md)), `core:designsystem`, `core:ui`, `:shared` (mini-player overlay).

## Known Issues

- Gapless/crossfade chưa cấu hình.
- Cast/AirPlay, CarPlay/Android Auto thuộc giai đoạn sau.
- Cần map kỹ trạng thái buffering/error giữa hai nền tảng về `PlaybackState` chung.
