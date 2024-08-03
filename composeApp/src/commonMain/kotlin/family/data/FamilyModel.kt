package family.data

import androidx.compose.runtime.mutableStateOf
import course.data.Term
import family.domain.FamilyUiState
import family.domain.StudentInput
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Family(
    val familyName: String,
    val city: String,
    val students: List<Student>,
    val defaultStart: LocalDate? = null,
    val defaultEnd: LocalDate? = null,
) {
    fun toFamilyUiState() = FamilyUiState(
        familyName = mutableStateOf(familyName),
        city = mutableStateOf(city),
        students = mutableStateOf(students.map { it.toStudentInput() }),
        defaultStart = defaultStart,
        defaultEnd = defaultEnd
    )
}

data class Student(
    val name: String,
    val grade: StudentGrade,
    val allTerms: List<Term>? = null,
    val activeTerm: Term? = null,
) {
    fun toStudentInput() = StudentInput(
        name = mutableStateOf(name),
        gradeLevel = mutableStateOf(grade),
    )
}

enum class StudentGrade {
    PreK, Kindergarten, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth
}

enum class CourseGrade(val display: String) {
    A("A"), AMinus("A-"), BPlus("B+"), B("B"), BMinus("B-"), CPlus("C+"), C("C"), CMinus("C-"), DPlus(
        "D+"
    ),
    D("D"), DMinus(
        "D-"
    ),
    F("F")
}
