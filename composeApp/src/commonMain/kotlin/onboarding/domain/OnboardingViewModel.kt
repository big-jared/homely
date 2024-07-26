package onboarding.domain

import cafe.adriel.voyager.core.model.ScreenModel
import course.presentation.CoursesSetupStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import onboarding.data.OnboardingRepository
import onboarding.presentation.onboardingStep.Community
import family.presentation.FamilyInfo
import onboarding.presentation.onboardingStep.OnboardingStep
import onboarding.presentation.onboardingStep.StateAndLegal
import onboarding.presentation.onboardingStep.Subscription
import onboarding.presentation.onboardingStep.WelcomeStep

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
) : ScreenModel {
    private var activeIndex = 0
    private val _activeIndexFlow = MutableStateFlow(activeIndex)
    val activeIndexFlow: StateFlow<Int> = _activeIndexFlow

    private val _complete = MutableStateFlow(false)
    val complete: StateFlow<Boolean> = _complete

    private val _initialized = MutableStateFlow(false)
    val initialized: StateFlow<Boolean> = _initialized

    val steps = listOf(
        WelcomeStep(), FamilyInfo(), CoursesSetupStep(), StateAndLegal(), Community(), Subscription()
    )

    fun first() = steps.first()

    fun current() = steps[activeIndex]

    fun isLast(): Boolean = activeIndex == (steps.size - 1)

    suspend fun goTo(step: OnboardingStep) {
        current().goTo(steps[activeIndex])
        activeIndex = steps.indexOf(step)
        _activeIndexFlow.emit(activeIndex)
    }

    suspend fun initialize() {
        _complete.value = onboardingRepository.isCompleted()
        _initialized.value = true
    }

    suspend fun back() {
        if (activeIndex > 0) activeIndex -= 1
        _activeIndexFlow.emit(activeIndex)
        current().pop()
    }

    suspend fun proceed(): OnboardingStep? {
        return if (activeIndex == steps.size - 1) {
            onboardingRepository.setComplete()
            _complete.emit(true)
            null
        } else {
            activeIndex += 1
            _activeIndexFlow.emit(activeIndex)
            steps[activeIndex]
        }
    }
}