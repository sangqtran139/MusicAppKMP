package com.sangtq.musicappkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.sangtq.musicappkmp.app.MainScaffold
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import com.sangtq.musicappkmp.feature.auth.presentation.AuthFlow
import org.koin.compose.KoinContext

/**
 * Root composable. Koin khởi tạo ở platform entry; [KoinContext] bắc cầu cho composable.
 * Auth = cổng local stub (xem docs/Roadmap.md).
 */
@Composable
fun App() {
    KoinContext {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }
        AppTheme {
            var loggedIn by remember { mutableStateOf(false) }
            if (loggedIn) MainScaffold() else AuthFlow(onAuthenticated = { loggedIn = true })
        }
    }
}
