package course.data

import family.data.FamilyRepository
import family.data.Student
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SchoolingRepository(private val courseDataSource: CourseDataSource): KoinComponent {
    // Just using this to get the students
    private val familyRepository by inject<FamilyRepository>()

    val terms: Map<Student, Set<Term>> get() = _terms
    private var _terms = mutableMapOf<Student, Set<Term>>()

    suspend fun initialize() {
        _terms = familyRepository.currentFamily?.students?.associateWith {
            courseDataSource.getTermsForStudent(it)
        }?.toMutableMap() ?: mutableMapOf()
    }

    suspend fun updateTerm(student: Student, term: Term) {
        courseDataSource.setTermForStudent(term, student)
    }
}
