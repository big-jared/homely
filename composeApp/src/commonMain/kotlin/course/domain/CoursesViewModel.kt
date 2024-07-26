package course.domain

import cafe.adriel.voyager.core.model.ScreenModel
import course.data.SchoolingRepository
import family.data.FamilyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import state.data.StateRepository

class CoursesViewModel(
    private val familyRepository: FamilyRepository,
    private val schoolingRepository: SchoolingRepository,
    private val stateRepository: StateRepository
) : ScreenModel {
    val uiState = MutableStateFlow<CoursesUiState?>(null)
    val current get() = uiState.value?.currentTerm

    suspend fun initialize() {
        uiState.value = CoursesUiState(
            studentTerms = familyRepository.currentFamily?.students?.map { student ->
                TermUiState(
                    student = student,
                )
            } ?: emptyList()
        )
    }

    suspend fun selectDefaultSyllabusForTerm() {
        val current = current ?: return
        current.courses.value = stateRepository.getRequiredCourses(
            family = familyRepository.currentFamily ?: return,
            student = current.student
        ).map { it.toCourseUiState() }
    }

    fun addCourse(course: CourseUiState) {
        val current = current ?: return
        current.courses.value = current.courses.value.toMutableList().apply {
            if (!contains(course)) {
                add(course)
            }
        }
    }

    fun removeCourse(course: CourseUiState) {
        val current = current ?: return
        current.courses.value = current.courses.value.toMutableList().apply {
            remove(course)
        }
    }

    fun isValid(): Boolean {
        val currentTerm = current ?: return false
        return currentTerm.courses.value.all { it.isValid() }
                && currentTerm.startDate.value != null
                && currentTerm.endDate.value != null
                && currentTerm.termName.value.isNotEmpty()
    }
}