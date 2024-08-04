package onboarding.presentation.onboardingStep

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import common.BasicAlertDialog
import kotlinx.coroutines.launch
import onboarding.domain.OnboardingViewModel
import onboarding.presentation.OnboardingResult
import onboarding.presentation.isHorizontalLayout
import org.koin.compose.koinInject

abstract class OnboardingStep(val displayOnly: Boolean = false) : Screen {
    abstract val name: String
    abstract val contentCta: String
    val canContinue: MutableState<Boolean> = mutableStateOf(false)
    val continueSuffix: MutableState<String?> = mutableStateOf(null)
    lateinit var navigator: Navigator

    fun pop() {
        navigator.pop()
    }

    fun goTo(step: OnboardingStep) {
        navigator.push(step)
    }

    @Composable
    override fun Content() {
        val onboardingViewModel = koinInject<OnboardingViewModel>()
        val coScope = rememberCoroutineScope()
        this.navigator = LocalNavigator.currentOrThrow
        var dialogState by remember { mutableStateOf<OnboardingResult.Failure?>(null) }

        Column(modifier = Modifier.fillMaxSize()) {
            if (displayOnly) {
                OnboardingContent()
                return
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                Column(
                    modifier = Modifier.align(
                        if (isHorizontalLayout()) Alignment.Start else Alignment.CenterHorizontally
                    )
                        .widthIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 20.dp)
                ) {
                    if (contentCta.isNotBlank()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Text(
                                modifier = Modifier.align(Alignment.TopStart),
                                text = contentCta,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                    }
                    OnboardingContent()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .height(54.dp),
                enabled = canContinue.value,
                content = {
                    Text(text = if (onboardingViewModel.isLast()) "Finish" else "Continue")
                },
                onClick = {
                    coScope.launch {
                        continueToNextStep(navigator, onboardingViewModel) {
                            dialogState = it
                        }
                    }
                }
            )
            dialogState?.let {
                BasicAlertDialog(it.title, it.message, onClose = {
                    dialogState = null
                })
            }
        }
    }

    abstract suspend fun evaluateContinue(): OnboardingResult

    @Composable
    abstract fun ColumnScope.OnboardingContent()

    protected suspend fun continueToNextStep(
        navigator: Navigator,
        viewModel: OnboardingViewModel,
        onFailure: (OnboardingResult.Failure) -> Unit = {}
    ) {
        when (val result = evaluateContinue()) {
            is OnboardingResult.Success -> {
                navigator.push(viewModel.proceed() ?: return)
            }

            is OnboardingResult.Failure -> {
                onFailure(result)
            }
        }
    }
}
