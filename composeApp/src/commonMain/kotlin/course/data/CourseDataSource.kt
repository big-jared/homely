package course.data

import family.data.Student
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import state.data.demoCourses
import kotlin.time.Duration.Companion.days

interface CourseDataSource {
    suspend fun getTermsForStudent(student: Student): Set<Term>
    suspend fun setTermForStudent(term: Term, student: Student)
}

class DemoCourseDataSource(private val useDefaults: Boolean = false) : CourseDataSource {
    private var currentTerms = mutableMapOf<Student, Term>()

    private val defaultTerm = Term(
        name = "Term",
        courses = demoCourses.toSet(),
        startDate = now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        endDate = now().plus(200.days).toLocalDateTime(TimeZone.currentSystemDefault()).date,
    )

    override suspend fun getTermsForStudent(student: Student): Set<Term> {
        return if (useDefaults) {
            setOf(defaultTerm)
        } else emptySet()
    }

    override suspend fun setTermForStudent(term: Term, student: Student) {
        currentTerms[student] = term
    }
}
