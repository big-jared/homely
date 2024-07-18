package onboarding.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import application.presentation.AuthenticatedScreen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.materialkolor.ktx.harmonize
import common.AppIconButton
import common.FullScreenProgressIndicator
import common.HighlightBox
import dashboard.presentation.DashboardScreen
import getPlatform
import kotlinx.coroutines.launch
import onboarding.domain.OnboardingViewModel
import onboarding.presentation.onboardingStep.OnboardingStep
import org.koin.compose.koinInject


sealed class OnboardingResult {
    data object Success : OnboardingResult()
    class Failure(val title: String = "Unable to Continue", val message: String) :
        OnboardingResult()
}

class OnboardingScreen() : AuthenticatedScreen {
    @Composable
    override fun ScreenContent() {
        val viewModel = koinInject<OnboardingViewModel>()
        val initialized = viewModel.initialized.collectAsState()
        val complete = viewModel.complete.collectAsState()
        val activeStepIndex = viewModel.activeIndexFlow.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val coScope = rememberCoroutineScope()

        LaunchedEffect(null) {
            viewModel.initialize()
        }

        if (!initialized.value) {
            FullScreenProgressIndicator()
            return
        }

        if (complete.value) {
            navigator.replaceAll(DashboardScreen())
            return
        }

        if (isHorizontalLayout()) {
            Row {
                AnimatedVisibility(activeStepIndex.value != 0) {
                    Column(
                        modifier = Modifier.fillMaxHeight().widthIn(max = 260.dp)
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(top = 24.dp)
                    ) {
                        viewModel.steps.forEachIndexed { index, step ->
                            StepRow(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                    .padding(horizontal = 24.dp),
                                step = step,
                                active = index == activeStepIndex.value,
                                viewModel = viewModel
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxHeight().weight(1f)) {
                    Navigator(viewModel.first()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CurrentScreen()
                        }
                    }
                }
            }
        } else {
            Column {
                AnimatedVisibility(activeStepIndex.value != 0) {
                    Row(modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.primary)) {
                        Row(
                            modifier = Modifier.heightIn(max = 144.dp)
                                .horizontalScroll(rememberScrollState())
                                .padding(start = 16.dp)
                        ) {
                            if (!isHorizontalLayout()) {
                                AnimatedVisibility(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    visible = viewModel.activeIndexFlow.value > 1
                                ) {
                                    AppIconButton(
                                        modifier = Modifier.padding(end = 8.dp).size(42.dp),
                                        painter = rememberVectorPainter(Icons.AutoMirrored.Rounded.ArrowBack),
                                        containerColor = MaterialTheme.colorScheme.background,
                                        onClick = {
                                            coScope.launch {
                                                viewModel.back()
                                            }
                                        })
                                }
                            }
                            viewModel.steps.forEachIndexed { index, step ->
                                if (index >= activeStepIndex.value) {
                                    StepRow(
                                        modifier = Modifier.padding(end = 8.dp)
                                            .padding(vertical = 24.dp)
                                            .align(Alignment.CenterVertically),
                                        step = step,
                                        active = index == activeStepIndex.value,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Navigator(viewModel.first()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CurrentScreen()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StepRow(modifier: Modifier = Modifier, step: OnboardingStep, active: Boolean, viewModel: OnboardingViewModel) {
        val coScope = rememberCoroutineScope()
        HighlightBox(
            modifier = modifier,
            text = step.name,
            backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = if (active) 1f else .4f),
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                if (!isHorizontalLayout()) return@HighlightBox
                coScope.launch {
                    viewModel.goTo(step)
                }
            }
        )
    }
}

fun isHorizontalLayout(): Boolean =
    getPlatform().name.contains("Wasm") || getPlatform().name.contains("Desktop")