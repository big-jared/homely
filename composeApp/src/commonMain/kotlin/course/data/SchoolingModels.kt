package course.data

import course.domain.CourseUiState
import course.domain.TermUiState
import course.domain.UiSyllabus
import course.domain.UiSyllabusItem
import family.data.CourseGrade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.random.Random
import kotlin.time.Duration

data class Term(
    val id: Int,
    val name: String,
    val courses: Set<Course>,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val active: Boolean = true,
) {
    fun gpa(): String = "TODO"

    fun toTermUiState() = TermUiState(
        termName = MutableStateFlow(name),
        courses = MutableStateFlow(courses.map { it.toCourseUiState() }),
        startDate = MutableStateFlow(startDate),
        endDate = MutableStateFlow(endDate),
        existingTerm = this
    )

    override fun equals(other: Any?): Boolean {
        return if (other is Term && other.id == this.id) true else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + courses.hashCode()
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (endDate?.hashCode() ?: 0)
        result = 31 * result + active.hashCode()
        return result
    }
}

data class Course(
    val name: String,
    val syllabus: Syllabus,
    val assignments: List<Assignment>,
    val color: Int,
    val description: String? = null,
    val time: LocalTime? = null,
    val duration: Duration? = null,
    val interval: ClassInterval,
    val gradeScale: GradeScale,
) {
    fun toCourseUiState() = CourseUiState(
        courseName = MutableStateFlow(name),
        syllabus = MutableStateFlow(syllabus.toUiSyllabus()),
        description = MutableStateFlow(description ?: ""),
        color = MutableStateFlow(color)
    )
}

enum class SyllabusType {
    PointBased, WeightBased()
}

data class Syllabus(
    val type: SyllabusType,
    val items: List<SyllabusItem>
) {
    fun toUiSyllabus() = UiSyllabus(
        type = MutableStateFlow(SyllabusType.WeightBased),
        items = MutableStateFlow(items.map { it.toUiSyllabusItem() })
    )
}

data class SyllabusItem(
    val name: String, val percentage: Int
) {
    fun toUiSyllabusItem() = UiSyllabusItem(
        name = MutableStateFlow(name), percentage = MutableStateFlow(percentage)
    )
}

data class ClassInterval(
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
) {
    fun workingDays(): String {
        return "${if (monday) "M" else ""},${if (tuesday) "T" else ""},${if (wednesday) "W" else ""},${if (thursday) "Th" else ""},${if (friday) "F" else ""},${if (saturday) "S" else ""},${if (sunday) "Su" else ""}"
    }

    companion object {
        fun weekDays(): ClassInterval = ClassInterval(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = false,
            sunday = false
        )
    }
}

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

data class Assignment(
    val name: String,
    val received: Int? = null,
    val possible: Int,
    val notes: String,
    val dueDate: Instant? = null,
    val duration: Duration? = null,
)