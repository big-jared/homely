package onboarding.presentation.onboardingStep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import onboarding.presentation.OnboardingResult

class Community : OnboardingStep() {
    override val name = "Community"
    override val contentCta = "Get plugged in with local families in your area"

    override suspend fun evaluateContinue(): OnboardingResult {
        return OnboardingResult.Success
    }

    @Composable
    override fun ColumnScope.OnboardingContent() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Cyan))
    }
}