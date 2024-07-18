package onboarding.presentation.onboardingStep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import onboarding.presentation.OnboardingResult

class Subscription : OnboardingStep() {
    override val name = "Subscription"
    override val contentCta = "How do you want to use Homely?"

    override suspend fun evaluateContinue(): OnboardingResult {
        return OnboardingResult.Success
    }

    @Composable
    override fun ColumnScope.OnboardingContent() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Green))
    }
}
