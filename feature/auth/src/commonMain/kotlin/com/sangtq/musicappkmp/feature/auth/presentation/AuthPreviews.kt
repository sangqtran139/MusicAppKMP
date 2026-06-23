package com.sangtq.musicappkmp.feature.auth.presentation

import androidx.compose.runtime.Composable
import com.sangtq.musicappkmp.core.designsystem.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun AuthFlowPreview() {
    AppTheme {
        AuthFlow(onAuthenticated = {})
    }
}
