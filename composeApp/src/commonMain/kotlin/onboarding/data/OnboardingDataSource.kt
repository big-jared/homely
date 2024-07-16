package onboarding.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import application.presentation.AuthenticatedScreen
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import common.FullScreenProgressIndicator
import dashboard.presentation.DashboardScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

abstract class OnboardingStep : Screen {
    abstract val name: String
}

class FamilyInfo : OnboardingStep() {
    override val name = "Family Info"

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Red))
    }
}

class StateAndLegal : OnboardingStep() {
    override val name = "State Registration"

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Blue))
    }
}

class Community : OnboardingStep() {
    override val name = "Community"

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Cyan))
    }
}

class StudentSetup : OnboardingStep() {
    override val name = "Students"

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Yellow))
    }
}

class Subscription : OnboardingStep() {
    override val name = "Subscription"

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Green))
    }
}

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
) : ScreenModel {
    private val _activeStepIndex = MutableStateFlow(0)
    val activeStep: Flow<OnboardingStep> = _activeStepIndex.map { steps[it] }

    private val _complete = MutableStateFlow(false)
    val complete: StateFlow<Boolean> = _complete

    private val _initialized = MutableStateFlow(false)
    val initialized: StateFlow<Boolean> = _initialized

    val steps = listOf(
        FamilyInfo(),
        StateAndLegal(),
        StudentSetup(),
        Community(),
        Subscription()
    )

    fun first() = steps.first()

    suspend fun initialize() {
        _complete.value = onboardingRepository.isCompleted()
        _initialized.value = true
    }

    suspend fun proceed() {
        if (_activeStepIndex.value == steps.size - 1) {
            onboardingRepository.setComplete()
        } else {
            _activeStepIndex.emit(_activeStepIndex.value + 1)
        }
    }
}

class OnboardingScreen() : AuthenticatedScreen {
    @Composable
    override fun ScreenContent() {
        val viewModel = koinInject<OnboardingViewModel>()
        val activeStep = viewModel.activeStep.collectAsState(viewModel.first())
        val initialized = viewModel.initialized.collectAsState()
        val complete = viewModel.initialized.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(null) {
            viewModel.initialize()
        }

        if (complete.value) {
            navigator.replaceAll(DashboardScreen())
            return
        }

        if (!initialized.value) {
            FullScreenProgressIndicator()
            return
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                viewModel.steps.forEach { step ->
                    StepHeader(step, active = activeStep.value == step)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Navigator(viewModel.first()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }

    @Composable
    fun OnboardingSection(step: OnboardingStep) {

    }

    @Composable
    fun StepHeader(step: OnboardingStep, active: Boolean) {
        Text(step.name)
        if (active) {
            Text("ACTIVE")
        }
    }
}