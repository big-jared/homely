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

    suspend fun update() {
        val family = familyUiState.value ?: throw Exception("Family Info not set!")
        familyRepository.update(family.toFamily())
    }
}
