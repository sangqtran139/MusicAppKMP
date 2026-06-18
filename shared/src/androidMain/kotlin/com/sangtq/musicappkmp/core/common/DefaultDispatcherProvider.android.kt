package com.sangtq.musicappkmp.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
