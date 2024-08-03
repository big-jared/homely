package course.data

import family.data.Course
import family.data.Student
import family.data.Term

class SchoolingRepository(private val courseDataSource: CourseDataSource) {
    val terms: Map<Student, Set<Course>> get() = _terms
    private var _terms = mutableMapOf<Student, Set<Course>>()

    suspend fun initialize(): List<Term> {
        return emptyList()
    }

    suspend fun getTermForStudent(student: Student) {
    }

    suspend fun 
}
