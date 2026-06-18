# ADR-0010: Playback native qua expect/actual

- **Trạng thái:** Accepted — 2026-06-18

## Context

Playback là trái tim sản phẩm và gắn chặt với từng OS: phát nền, media notification / Control Center,
audio focus / interruption, đổi route (tai nghe, Bluetooth, CarPlay/Android Auto), gapless.

## Problem

Không có thư viện cross-platform nào đạt chất lượng native trên tất cả các khía cạnh đó.

## Decision

Định nghĩa interface `AudioPlayer` + `AudioPlayerFactory` (`expect`) trong `:core:playback`
(`commonMain`). `actual`: **Media3 ExoPlayer + MediaSessionService** trên Android, **AVQueuePlayer +
AVAudioSession** trên iOS. State playback expose qua `StateFlow<PlaybackState>` dùng chung. Không
ViewModel/UseCase nào tham chiếu ExoPlayer/AVPlayer.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Thư viện audio KMP cross-platform | Chưa chín; tích hợp OS yếu (notification, focus, Auto/CarPlay) — không hy sinh được với core feature |
| Media API của Compose Multiplatform | Không đủ cho nhu cầu music player |

## Consequences

- **Tích cực:** playback và tích hợp OS tốt nhất mỗi nền tảng; interface chung giữ ViewModel/UseCase platform-agnostic; downloads offline sau này chỉ đổi nguồn URL, không đổi interface.
- **Đánh đổi:** hai implementation phải maintain và test; cần map cẩn thận trạng thái (buffering, error) về `PlaybackState` chung.
- **Khả năng mở rộng:** năng lực mới (cast, equalizer) mở rộng interface một lần, có hai implementation; nền tảng thứ ba chỉ thêm một `actual`.
