package com.sangtq.musicappkmp.core.common

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Trừu tượng hoá dispatcher để inject (dễ test, không hardcode `Dispatchers.IO`).
 * Implementation theo nền tảng được cung cấp khi wiring Koin (Phase 0 — DI).
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}
