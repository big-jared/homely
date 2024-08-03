package course.domain

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import course.data.SchoolingRepository
import family.data.FamilyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import onboarding.presentation.OnboardingResult
import state.data.StateRepository

class CoursesViewModel(
    private val familyRepository: FamilyRepository,
    private val schoolingRepository: SchoolingRepository,
    private val stateRepository: StateRepository
) : ScreenModel {
    val uiState = MutableStateFlow<CoursesUiState?>(null)
    val currentTerm get() = uiState.value?.currentTerm

    suspend fun initialize() {
        val currentFamily = familyRepository.currentFamily ?: return
        uiState.value = CoursesUiState(
            studentTerms = currentFamily.students.map { student ->
                TermUiState(
                    student = student,
                    startDate = mutableStateOf(currentFamily.defaultStart),
                    endDate = mutableStateOf(currentFamily.defaultEnd)
                )
            }
        )
    }

    suspend fun update(): OnboardingResult {
        val term = currentTerm?.toTerm()

        schoolingRepository.

        return if (term.isValid()) {
            familyRepository.update(family.toFamily())
            OnboardingResult.Success
        } else {
            OnboardingResult.Failure(message = "Unexpected error occurred")
        }
    }

    suspend fun setDefaultDates() {
        val currentFamily = familyRepository.currentFamily ?: return
        familyRepository.update(
            currentFamily.copy(
                defaultStart = currentTerm?.startDate?.value ?: return,
                defaultEnd = currentTerm?.endDate?.value ?: return,
            )
        )
    }

    suspend fun selectDefaultSyllabusForTerm() {
        val current = currentTerm ?: return
        current.courses.value = stateRepository.getRequiredCourses(
            family = familyRepository.currentFamily ?: return,
            student = current.student
        ).map { it.toCourseUiState() }
    }

    fun addCourse(course: CourseUiState) {
        val current = currentTerm ?: return
        current.courses.value = current.courses.value.toMutableList().apply {
            if (!contains(course)) {
                add(course)
            }
        }
    }

    fun removeCourse(course: CourseUiState) {
        val current = currentTerm ?: return
        current.courses.value = current.courses.value.toMutableList().apply {
            remove(course)
        }
    }

    fun isValid() = uiState.value?.currentTerm?.isValid() == true
}
