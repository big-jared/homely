package onboarding.di

import onboarding.data.DemoOnboardingDataSource
import onboarding.data.OnboardingDataSource
import onboarding.data.OnboardingRepository
import onboarding.domain.OnboardingViewModel
import org.koin.dsl.module

val onboardingModule = module(true) {
    single<OnboardingDataSource> { DemoOnboardingDataSource() }
    single { OnboardingRepository(get()) }
    single { OnboardingViewModel(get()) }
}