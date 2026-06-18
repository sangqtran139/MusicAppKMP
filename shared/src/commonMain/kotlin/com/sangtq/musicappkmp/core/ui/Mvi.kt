package com.sangtq.musicappkmp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Trạng thái bất biến của một màn hình. */
interface UiState

/** Tương tác của user/hệ thống. */
interface Intent

/** Side effect one-shot (navigate, snackbar…). */
interface Effect

/**
 * Base MVI ViewModel — hợp đồng state management DUY NHẤT của app (xem `docs/StateManagement.md`).
 * State chỉ đổi qua [setState]; effect one-shot qua [sendEffect]. Không tạo pattern khác.
 */
abstract class MviViewModel<S : UiState, I : Intent, E : Effect>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    abstract fun onIntent(intent: I)

    protected fun setState(reducer: S.() -> S) = _state.update(reducer)

    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
