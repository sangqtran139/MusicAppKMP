# TestingGuide

Test mỗi layer ở nơi rẻ nhất và ý nghĩa nhất. Đáy rộng (domain thuần), đỉnh hẹp (UI).

```
        ▲  UI Test (ít)           — luồng quan trọng mỗi màn hình
       ───
      ─────  ViewModel Test       — Intent → UiState/Effect (Turbine)
     ───────
    ─────────  Data Test          — Repository, Mapper, cache decision
   ───────────
  ─────────────  Domain/UseCase   — logic thuần, JVM nhanh, không nền tảng
```

## Unit Test

### Domain (UseCase) — nhiều nhất, ở `commonTest`

Dùng **fake repository** (ưu tiên fake hơn mock trong common code).

```kotlin
class GetTrackUseCaseTest {
    @Test
    fun returns_track_on_success() = runTest {
        val repo = FakeTrackRepository(tracks = listOf(sampleTrack))
        val result = GetTrackUseCase(repo)("1")
        assertEquals(AppResult.Success(sampleTrack), result)
    }

    @Test
    fun returns_failure_when_missing() = runTest {
        val result = GetTrackUseCase(FakeTrackRepository(emptyList()))("x")
        assertTrue(result is AppResult.Failure)
    }
}
```

### Mapper — round-trip, thuần, rẻ

```kotlin
@Test
fun dto_maps_to_domain() {
    val dto = TrackDto(id = "1", title = "A", artist = "B", artwork = null)
    assertEquals(Track("1", "A", "B", null), dto.toDomain())
}
```

## Integration Test (Data layer)

- Test `RepositoryImpl`: quyết định cache vs network, map lỗi → `AppError`.
- Dùng **fake `*Api`** + **in-memory SQLDelight driver** (`inMemoryDriver`) trong `commonTest`/platform test.

```kotlin
@Test
fun observe_emits_cache_then_refreshes() = runTest {
    val api = FakeCatalogApi(remote = listOf(trackDtoA))
    val dao = TrackDao(inMemoryDriver())
    val repo = TrackRepositoryImpl(api, dao, TestDispatchers)
    repo.observeTracks().test {
        assertEquals(emptyList(), (awaitItem() as AppResult.Success).data)   // cache rỗng
        assertEquals(listOf(trackA), (awaitItem() as AppResult.Success).data) // sau khi fetch+save
        cancelAndIgnoreRemainingEvents()
    }
}
```

## ViewModel Test (Presentation)

Dùng `kotlinx-coroutines-test` (`runTest`) + **Turbine** để assert chuỗi `UiState` và `Effect`.

```kotlin
@Test
fun play_click_sets_playing() = runTest {
    val vm = PlayerViewModel(playTrack = FakePlayTrackUseCase.success())
    vm.state.test {
        assertFalse(awaitItem().isPlaying)        // initial
        vm.onIntent(PlayerIntent.PlayClicked)
        assertTrue(awaitItem().isLoading)
        assertTrue(awaitItem().isPlaying)
        cancelAndIgnoreRemainingEvents()
    }
}

@Test
fun failure_emits_effect() = runTest {
    val vm = PlayerViewModel(playTrack = FakePlayTrackUseCase.failure(AppError.Network("x")))
    vm.effects.test {
        vm.onIntent(PlayerIntent.PlayClicked)
        assertTrue(awaitItem() is PlayerEffect.ShowError)
    }
}
```

## UI Test (ít, giá trị cao)

- Dùng `compose-ui-test` cho luồng quan trọng mỗi feature (vd "play từ kết quả search").
- Ưu tiên test trên `*Content` (stateless) với state mẫu cho nhanh; chỉ test full `*Screen` cho luồng integration.

```kotlin
@Test
fun shows_play_button_when_track_loaded() = runComposeUiTest {
    setContent { PlayerContent(state = PlayerUiState(track = sampleTrack), onIntent = {}) }
    onNodeWithContentDescription("Play").assertIsDisplayed()
}
```

## Mocking Strategy

- **Common code**: ưu tiên **fake** thủ công (implement interface), đặt ở `commonTest/.../fakes/` và tái dùng.
- **Android/JVM unit test**: dùng **MockK** khi cần mock hành vi phức tạp.
- **DI graph**: một test `koin-test` chạy `verify()`/`checkModules()` để binding thiếu fail ở CI, không ở runtime.
- Thời gian/dispatcher: inject `Clock`/`DispatcherProvider`; không dùng `Dispatchers` thật hay wall-clock trong test.

## Không test

- Generated code (SQLDelight, serialization).
- Hành vi framework (recomposition của Compose, nội bộ Ktor).
- UseCase pass-through không có logic (đã cover qua ViewModel test).

## Lệnh chạy

- Tất cả: `./gradlew :shared:allTests`
- Android unit: `./gradlew :shared:testDebugUnitTest`
- iOS simulator: `./gradlew :shared:iosSimulatorArm64Test`
- Koin verify: nằm trong test suite chung.
