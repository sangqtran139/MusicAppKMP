package com.sangtq.musicappkmp.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.sangtq.musicappkmp.core.designsystem.theme.AppSpacing

/**
 * Cổng đăng nhập local (stub) theo Figma welcome/login. KHÔNG gọi backend — Deezer không có
 * tài khoản user (xem docs/Roadmap.md). [onAuthenticated] mở app chính.
 */
@Composable
fun AuthFlow(onAuthenticated: () -> Unit, modifier: Modifier = Modifier) {
    var step by remember { mutableStateOf(AuthStep.Welcome) }
    when (step) {
        AuthStep.Welcome -> WelcomeContent(onGetStarted = { step = AuthStep.Login }, modifier = modifier)
        AuthStep.Login -> LoginContent(onLogin = onAuthenticated, modifier = modifier)
    }
}

private enum class AuthStep { Welcome, Login }

@Composable
private fun WelcomeContent(onGetStarted: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().safeContentPadding().padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Text(
            "musium",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f).padding(top = AppSpacing.xl),
        )
        Text(
            "From the latest to the greatest hits, play your favorite tracks on musium now!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = AppSpacing.lg),
        )
        Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth()) {
            Text("Get Started")
        }
        Spacer(Modifier.padding(AppSpacing.sm))
    }
}

@Composable
private fun LoginContent(onLogin: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().safeContentPadding().padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Let's get you in",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = AppSpacing.xl),
        )
        listOf("Continue with Google", "Continue with Facebook", "Continue with Apple").forEach { label ->
            OutlinedButton(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.xs),
            ) { Text(label) }
        }
        Spacer(Modifier.padding(AppSpacing.md))
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Log in with a password")
        }
    }
}
