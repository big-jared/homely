package onboarding.presentation.onboardingStep

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import common.AppIconButton
import onboarding.presentation.OnboardingResult


class CoursesSetup : OnboardingStep() {
    override val name = "Course Setup"
    override val contentCta = "Plan your students courses"

    override suspend fun evaluateContinue(): OnboardingResult {
        return OnboardingResult.Success
    }

    @Composable
    override fun ColumnScope.OnboardingContent() {
        Text("Students")
        Column {
            Text("NONE SET")
        }
        AppIconButton(onClick = {
            // add student
        })
    }
}