package com.sangtq.musicappkmp.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Implementation mặc định. `io` lấy theo nền tảng qua [ioDispatcher]. */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = ioDispatcher()
}

internal expect fun ioDispatcher(): CoroutineDispatcher
