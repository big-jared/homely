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
    private var currentTerms = mutableMapOf<Student, Set<Term>>()

    private val defaultTerm = Term(
        id = 123141231,
        name = "Term",
        courses = demoCourses.toSet(),
        startDate = now().toLocalDateTime(TimeZone.UTC).date,
        endDate = now().plus(200.days).toLocalDateTime(TimeZone.UTC).date,
    )

    override suspend fun getTermsForStudent(student: Student): Set<Term> {
        return if (useDefaults) {
            setOf(defaultTerm)
        } else currentTerms[student] ?: return emptySet()
    }

    override suspend fun setTermForStudent(term: Term, student: Student) {
        currentTerms[student] = (currentTerms[student] ?: emptySet()).toMutableSet().also { terms ->

            // Theoretically a Set is unique, and this shouldn't be necessary, but just doing terms.add(term) and relying
            // on equals wasn't working for some reason. So this for now
            terms.remove(terms.firstOrNull { it.id == term.id })
            terms.add(term)
        }
    }
}
