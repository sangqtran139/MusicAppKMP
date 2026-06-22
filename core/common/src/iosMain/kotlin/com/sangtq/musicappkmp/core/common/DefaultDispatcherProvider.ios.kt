package com.sangtq.musicappkmp.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Native chưa có Dispatchers.IO chuyên dụng → dùng Default (Ktor/Darwin tự xử lý I/O off-main).
internal actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.Default
