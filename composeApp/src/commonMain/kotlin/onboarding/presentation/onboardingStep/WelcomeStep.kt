package onboarding.presentation.onboardingStep

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import application.presentation.DarkTheme
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.materialkolor.ktx.darken
import kotlinx.coroutines.launch
import onboarding.domain.OnboardingViewModel
import onboarding.presentation.OnboardingResult
import org.koin.compose.koinInject

class WelcomeStep : OnboardingStep(displayOnly = true) {
    override val name: String = "Welcome"
    override val contentCta = ""

    override suspend fun evaluateContinue() = OnboardingResult.Success

    @Composable
    override fun ColumnScope.OnboardingContent() {
        val onboardingViewModel = koinInject<OnboardingViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()

        DarkTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary.darken(1.5f)) {
                Box {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = "Glad you made it!",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "Let's get you set up",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
                        )
                        FilledTonalButton(
                            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 12.dp),
                            onClick = {
                                coScope.launch {
                                    continueToNextStep(navigator, onboardingViewModel)
                                }
                            },
                            modifier = Modifier.padding(vertical = 32.dp).padding(top = 64.dp).align(
                                Alignment.CenterHorizontally
                            )
                        ) {
                            Text(
                                "Start Onboarding",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = rememberVectorPainter(Icons.AutoMirrored.Rounded.ArrowForward),
                                contentDescription = "Start Onboarding"
                            )
                        }
                    }
                }
            }
        }
    }
}
