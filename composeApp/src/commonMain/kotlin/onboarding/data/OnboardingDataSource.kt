package onboarding.data

interface OnboardingDataSource {
    suspend fun isCompleted(): Boolean
    suspend fun setComplete()
}

class DemoOnboardingDataSource : OnboardingDataSource {
    private var complete = false

    override suspend fun isCompleted(): Boolean = complete
    override suspend fun setComplete() {
        this.complete = true
    }
}
