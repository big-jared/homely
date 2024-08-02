package family.domain

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import family.data.FamilyRepository
import onboarding.presentation.OnboardingResult

class FamilyInfoViewModel(private val familyRepository: FamilyRepository) : ScreenModel {
    val familyUiState = mutableStateOf<FamilyUiState?>(null)

    suspend fun initialize() {
        familyRepository.initialize()
        familyUiState.value = familyRepository.currentFamily?.toFamilyUiState() ?: FamilyUiState()
    }

    suspend fun update(): OnboardingResult {
        val family = familyUiState.value ?: return OnboardingResult.Failure(message = "Unexpected Error Occurred, try again")

        return if (family.isValid()) {
            familyRepository.update(family.toFamily())
            OnboardingResult.Success
        } else {
            OnboardingResult.Failure(message = "Unexpected error occurred")
        }
    }

    fun isValid() = familyUiState.value?.isValid() == true
}
