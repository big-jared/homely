package onboarding.data

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import application.presentation.AuthenticatedScreen
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import common.AppIconButton
import common.FullScreenProgressIndicator
import dashboard.presentation.DashboardScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

abstract class OnboardingStep(val isFinal: Boolean = false) : Screen {
    abstract val name: String

    @Composable
    override fun Content() {
        val onboardingViewModel = koinInject<OnboardingViewModel>()
        val coScope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(
                    modifier = Modifier,
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.padding(top = 32.dp))
                OnboardingContent()
            }

            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                content = {
                    Text(text = if (isFinal) "Finish" else "Continue")
                },
                onClick = {
                    coScope.launch {
                        onboardingViewModel.proceed()
                    }
                }
            )
        }
    }

    @Composable
    abstract fun ColumnScope.OnboardingContent()
}

class FamilyInfo : OnboardingStep() {
    override val name = "Family Info"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun ColumnScope.OnboardingContent() {
        var familyName by remember { mutableStateOf("") }
        var zipcode by remember { mutableStateOf("") }
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        Row {
            Text(modifier = Modifier.weight(.5f), text = "Family Name")
            OutlinedTextField(
                modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = familyName,
                onValueChange = { familyName = it })
        }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.weight(.5f)) {
                Text(text = "Zipcode")
                TextButton(onClick = {
                    bottomSheetNavigator.show(ZipcodeBottomsheet())
                }) {
                    Text("Why?")
                }
            }
            OutlinedTextField(
                modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = zipcode,
                onValueChange = { zipcode = it })
        }
    }
}

class ZipcodeBottomsheet(): Screen {
    @Composable
    override fun Content() {
        Column(Modifier.padding(16.dp).padding(bottom = 32.dp)) {
            Text(modifier = Modifier.padding(top = 4.dp), text = "Why zipcode?", style = MaterialTheme.typography.titleLarge)
            Text(modifier = Modifier.padding(top = 16.dp), text = "Homely contains features that use location. Community, and state registration specifically. To better enable your experince please either grant location access to the app, or enter your zip code manually.")
        }
    }
}

class StateAndLegal : OnboardingStep() {
    override val name = "State Registration"

    @Composable
    override fun ColumnScope.OnboardingContent() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Blue))
    }
}

class Community : OnboardingStep() {
    override val name = "Community"

    @Composable
    override fun ColumnScope.OnboardingContent() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Cyan))
    }
}

class StudentSetup : OnboardingStep() {
    override val name = "Students"

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

class Subscription : OnboardingStep() {
    override val name = "Subscription"

    @Composable
    override fun ColumnScope.OnboardingContent() {
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
        val complete = viewModel.complete.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

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

        Column {
//            Row(modifier = Modifier.fillMaxWidth()) {
//                viewModel.steps.forEach { step ->
//                    StepHeader(step, active = activeStep.value == step)
//                }
//            }
//            Column(
//                modifier = Modifier.fillMaxWidth().weight(1f)
//            ) {
            Navigator(viewModel.first()) { navigator ->
                SlideTransition(navigator)
            }
//            }
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