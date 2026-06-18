# API

Tài liệu API backend của MusicAppKMP.

| Chủ đề | File |
| --- | --- |
| Deezer (qua RapidAPI) — endpoint, model, error, quota | [`DeezerApi.md`](DeezerApi.md) |

> Cách tích hợp (Ktor client, header, key handling, error mapping) nằm ở
> [`../NetworkingGuide.md`](../NetworkingGuide.md) và [`../ErrorHandling.md`](../ErrorHandling.md).
>
> **Tóm tắt:** catalog dùng **Deezer** qua proxy RapidAPI `deezerdevs-deezer`, auth bằng
> **static header** (`X-RapidAPI-Key` + `X-RapidAPI-Host`), **không OAuth/refresh**, chỉ có
> **5 endpoint** và playback là **preview mp3 ~30s**.
