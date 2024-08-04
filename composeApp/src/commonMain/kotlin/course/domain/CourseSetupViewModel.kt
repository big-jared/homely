package course.domain

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import course.data.SchoolingRepository
import course.data.Term
import family.data.FamilyRepository
import family.data.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import onboarding.presentation.OnboardingResult
import state.data.StateRepository

fun Map<Student, Set<Term>>.toCoursesUiState() = this.map { (student, terms) ->
    SchoolingUiState(
        student = student,
        uiTerm = terms.firstOrNull { it.active }?.toTermUiState()
            ?: TermUiState(termName = MutableStateFlow("${student.grade.name} Grade"))
    )
}

class CourseSetupViewModel(
    private val familyRepository: FamilyRepository,
    private val schoolingRepository: SchoolingRepository,
    private val stateRepository: StateRepository
) : ScreenModel {
    private val schoolingUiState = MutableStateFlow<List<SchoolingUiState>?>(null)

    // In the future currentStudentIndex could be saved to a datasource so that
    // A user can pick up where they left off
    private val currentStudentIndex = MutableStateFlow(0)
    val currentTermState = combine(schoolingUiState, currentStudentIndex) { uiState, currentStudent ->
        if (uiState == null) return@combine null
        uiState[currentStudent]
    }

    val nextTerm = combine(schoolingUiState, currentStudentIndex) { uiState, currentStudent ->
        if (uiState == null) return@combine null
        if (currentStudent >= uiState.size) return@combine null
        uiState[currentStudent + 1]
    }

    val currentTerm get() = schoolingUiState.value?.get(currentStudentIndex.value)

    suspend fun initialize() {
        schoolingRepository.initialize()
        schoolingUiState.value = schoolingRepository.terms.toCoursesUiState()
    }

    suspend fun update(): OnboardingResult {
        val termState = currentTerm ?: return OnboardingResult.Failure()

        return try {
            schoolingRepository.updateTerm(termState.student, termState.uiTerm.toTerm())
            OnboardingResult.Success
        } catch (e: Exception) {
            OnboardingResult.Failure()
        }
    }

    suspend fun setDefaultDates() {
        val currentFamily = familyRepository.currentFamily ?: return
        familyRepository.update(
            currentFamily.copy(
                defaultStart = currentTerm?.uiTerm?.startDate?.value ?: return,
                defaultEnd = currentTerm?.uiTerm?.endDate?.value ?: return,
            )
        )
    }

    suspend fun selectDefaultSyllabusForTerm() {
        val courses = currentTerm ?: return
        courses.uiTerm.courses.value = stateRepository.getRequiredCourses(
            family = familyRepository.currentFamily ?: return,
            student = courses.student
        ).map { it.toCourseUiState() }
    }

    fun addCourse(course: CourseUiState) {
        val courses = currentTerm?.uiTerm?.courses ?: return
        courses.value = courses.value.toMutableList().apply {
            if (!contains(course)) {
                add(course)
            }
        }
    }

    fun removeCourse(course: CourseUiState) {
        val courses = currentTerm?.uiTerm?.courses ?: return
        courses.value = courses.value.toMutableList().apply {
            remove(course)
        }
    }
}
