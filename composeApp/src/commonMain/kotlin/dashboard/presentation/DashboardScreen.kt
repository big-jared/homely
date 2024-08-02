package dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import application.presentation.AuthenticatedScreen

class DashboardScreen : AuthenticatedScreen {
    @Composable
    override fun ScreenContent() {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Blue))
    }
}
