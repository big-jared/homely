package family.data

import androidx.compose.runtime.mutableStateOf
import family.domain.FamilyUiState
import family.domain.StudentInput
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.time.Duration

data class Family(
    val familyName: String,
    val city: String,
    val students: List<Student>
) {
    fun toFamilyUiState() = FamilyUiState(
        familyName = mutableStateOf(familyName),
        city = mutableStateOf(city),
        students = mutableStateOf(students.map { it.toStudentInput() }),
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

data class Course(
    val name: String,
    val color: Int,
    val scale: GradeScale,
    val categories: List<GradeCategory>
)

data class Term(
    val name: String,
    val courses: List<Course>,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)

data class Assignment(
    val name: String,
    val received: Int? = null,
    val possible: Int,
    val notes: String,
    val dueDate: Instant? = null,
    val duration: Duration? = null,
)

data class GradeCategory(
    val name: String,
    val weight: Double,
    val assignments: List<Assignment>
)

data class GradeScale(
    val aThreshold: Double,
    val aMinusThreshold: Double,
    val bPlusThreshold: Double,
    val bThreshold: Double,
    val bMinusThreshold: Double,
    val cPlusThreshold: Double,
    val cThreshold: Double,
    val cMinusThreshold: Double,
    val dPlusThreshold: Double,
    val dThreshold: Double,
    val fThreshold: Double = 0.0,
) {
    fun resolveGrade(percentage: Double): CourseGrade = when (percentage) {
        in Double.MAX_VALUE..aThreshold -> CourseGrade.A
        in aThreshold..aMinusThreshold -> CourseGrade.AMinus
        in aMinusThreshold..bPlusThreshold -> CourseGrade.BPlus
        in bPlusThreshold..bThreshold -> CourseGrade.B
        in bThreshold..bMinusThreshold -> CourseGrade.BMinus
        in bMinusThreshold..cPlusThreshold -> CourseGrade.CPlus
        in cPlusThreshold..cThreshold -> CourseGrade.C
        in cThreshold..cMinusThreshold -> CourseGrade.CMinus
        in cMinusThreshold..dPlusThreshold -> CourseGrade.DPlus
        in dPlusThreshold..dThreshold -> CourseGrade.D
        else -> CourseGrade.F
    }
}

fun defaultGradeScale() = GradeScale(
    aThreshold = 93.0,
    aMinusThreshold = 90.0,
    bPlusThreshold = 87.0,
    bThreshold = 83.0,
    bMinusThreshold = 80.0,
    cPlusThreshold = 77.0,
    cThreshold = 73.0,
    cMinusThreshold = 70.0,
    dPlusThreshold = 67.0,
    dThreshold = 63.0
)

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