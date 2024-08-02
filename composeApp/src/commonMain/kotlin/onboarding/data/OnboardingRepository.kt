package onboarding.data

class OnboardingRepository(
    private val onboardingDataSource: OnboardingDataSource,
) {
    suspend fun isCompleted() = onboardingDataSource.isCompleted()
    suspend fun setComplete() = onboardingDataSource.setComplete()
}
