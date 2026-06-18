# StateManagement

Pattern duy nhất của dự án là **MVI**. Không tạo state management khác. Đây là hợp đồng mọi màn hình
phải tuân theo — copy, không biến tấu.

## State Management Strategy

- Mỗi màn hình có đúng **một** `UiState` bất biến (`data class`, mọi field có default).
- Mọi tương tác của user/hệ thống là một `Intent` (`sealed interface`).
- Side effect one-shot (navigate, snackbar) là một `Effect` (`sealed interface`).
- State chỉ đổi trong reducer của `MviViewModel` (`setState { copy(...) }`).
- State liên tục → `StateFlow`. Effect one-shot → `SharedFlow`/`Channel`.
- Composable là hàm thuần của `UiState` + callback `(Intent) -> Unit`.

## Base class (`core:ui`)

```kotlin
interface UiState
interface Intent
interface Effect

abstract class MviViewModel<S : UiState, I : Intent, E : Effect>(
    initialState: S,
) : ViewModel() {                                       // androidx.lifecycle KMP ViewModel

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()      // SharedFlow-like, one-shot

    protected val currentState: S get() = _state.value

    abstract fun onIntent(intent: I)

    protected fun setState(reducer: S.() -> S) { _state.update(reducer) }
    protected fun sendEffect(effect: E) { viewModelScope.launch { _effects.send(effect) } }
}
```

## Contract một màn hình (`XxxContract.kt`)

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
    data object Retry : PlayerIntent
}

sealed interface PlayerEffect : Effect {
    data class ShowError(val message: String) : PlayerEffect
    data object NavigateBack : PlayerEffect
}
```

## Luồng xử lý State

```
Intent ─▶ ViewModel.onIntent() ─▶ gọi UseCase ─▶ AppResult
                                       │
                          setState { copy(...) }   ← chỉ ở đây state mới đổi
                                       │
                            StateFlow<UiState> phát
                                       │
                  Composable collectAsStateWithLifecycle() ─▶ render
```

```kotlin
class PlayerViewModel(
    private val playTrack: PlayTrackUseCase,
) : MviViewModel<PlayerUiState, PlayerIntent, PlayerEffect>(PlayerUiState()) {

    override fun onIntent(intent: PlayerIntent) = when (intent) {
        PlayerIntent.PlayClicked  -> play()
        PlayerIntent.PauseClicked -> setState { copy(isPlaying = false) }
        is PlayerIntent.SeekTo    -> setState { copy(progress = intent.fraction) }
        PlayerIntent.Retry        -> play()
    }

    private fun play() {
        val id = currentState.track?.id ?: return
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = playTrack(id)) {
                is AppResult.Success -> setState { copy(isLoading = false, isPlaying = true) }
                is AppResult.Failure -> {
                    setState { copy(isLoading = false, error = result.error.message) }
                    sendEffect(PlayerEffect.ShowError(result.error.message))
                }
            }
        }
    }
}
```

## Luồng xử lý Event (Effect)

`Effect` dành cho việc phải xảy ra **đúng một lần** (navigate, snackbar). Không bao giờ để navigation
trong `UiState`.

```kotlin
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PlayerEffect.ShowError -> { /* show snackbar */ }
                PlayerEffect.NavigateBack -> onNavigateBack()
            }
        }
    }
    PlayerContent(state = state, onIntent = viewModel::onIntent)
}
```

## Best Practice

- Mọi field của `UiState` có default → reducer chỉ cần `copy()`.
- Loading/error là **field của state**, không phải state riêng (trừ khi màn hình thực sự modal).
- Đọc danh sách cache theo single-source-of-truth: quan sát DB (`Flow`), network chỉ cập nhật DB.
- Inject `DispatcherProvider`; chạy coroutine trong `viewModelScope`.
- Test reducer bằng Turbine (xem [`TestingGuide.md`](TestingGuide.md)).

## Anti Pattern (không được làm)

```kotlin
// ❌ Nhiều StateFlow rời rạc cho một màn hình → tổ hợp state không nhất quán
val isLoading = MutableStateFlow(false)
val track = MutableStateFlow<Track?>(null)
val error = MutableStateFlow<String?>(null)

// ❌ Đổi state ngoài reducer (trong Composable hay callback bất kỳ)
viewModel.state.value = newState

// ❌ Đưa sự kiện navigate vào UiState (sẽ phát lại khi recompose)
data class UiState(val navigateToDetail: Boolean = false)

// ❌ Composable tự giữ business state thay vì hoist lên ViewModel
var tracks by remember { mutableStateOf(repository.getTracks()) }   // gọi repo trong UI

// ❌ Ném exception ra khỏi UseCase/Repository thay vì trả AppResult.Failure
```
