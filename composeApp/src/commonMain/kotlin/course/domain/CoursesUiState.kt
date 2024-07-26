package course.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import common.red
import family.data.Student
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class TermUiState(
    val student: Student,
    val termName: MutableState<String> = mutableStateOf("${student.grade.name} Grade"),
    val startDate: MutableState<LocalDate?> = mutableStateOf(null),
    val endDate: MutableState<LocalDate?> = mutableStateOf(null),
    val courses: MutableState<List<CourseUiState>> = mutableStateOf(emptyList()),
) {
}

data class CoursesUiState(
    val studentTerms: List<TermUiState>,
    val selectedTerm: MutableState<Int> = mutableStateOf(0)
) {
    val currentTerm get() = studentTerms[selectedTerm.value]
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
}

data class UiClassInterval(
    val monday: MutableState<Boolean> = mutableStateOf(true),
    val tuesday: MutableState<Boolean> = mutableStateOf(true),
    val wednesday: MutableState<Boolean> = mutableStateOf(true),
    val thursday: MutableState<Boolean> = mutableStateOf(true),
    val friday: MutableState<Boolean> = mutableStateOf(true),
    val saturday: MutableState<Boolean> = mutableStateOf(false),
    val sunday: MutableState<Boolean> = mutableStateOf(false),
) {
    fun workingDays(): String {
        return "${if (monday.value) "M" else ""},${if (tuesday.value) "T" else ""},${if (wednesday.value) "W" else ""},${if (thursday.value) "Th" else ""},${if (friday.value) "F" else ""},${if (saturday.value) "S" else ""},${if (sunday.value) "Su" else ""}"
    }
}

data class CourseUiState(
    val courseName: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val time: MutableState<LocalTime?> = mutableStateOf(LocalTime(hour = 8, minute = 0)),
    val duration: MutableState<Duration> = mutableStateOf(1.hours),
    val interval: MutableState<UiClassInterval> = mutableStateOf(UiClassInterval()),
    val color: MutableState<Int> = mutableStateOf(red.toArgb()),
    val syllabus: MutableState<UiSyllabus> = mutableStateOf(UiSyllabus())
) {
    fun isValid(): Boolean {
        return courseName.value.isNotBlank() && syllabus.value.isValid()
    }
}

data class Course(
    val name: String,
    val syllabus: Syllabus,
    val color: Int,
    val description: String? = null,
) {
    fun toCourseUiState() = CourseUiState(
        courseName = mutableStateOf(name),
        syllabus = mutableStateOf(syllabus.toUiSyllabus()),
        description = mutableStateOf(description ?: ""),
        color = mutableStateOf(color)
    )
}

data class Syllabus(
    val items: List<SyllabusItem>
) {
    fun toUiSyllabus() = UiSyllabus(
        type = mutableStateOf(SyllabusType.WeightBased),
        items = mutableStateOf(items.map { it.toUiSyllabusItem() })
    )
}

enum class SyllabusType {
    PointBased, WeightBased()
}

data class UiSyllabus(
    val type: MutableState<SyllabusType> = mutableStateOf(SyllabusType.PointBased),
    val items: MutableState<List<UiSyllabusItem>> = mutableStateOf(listOf(UiSyllabusItem()))
) {
    fun isValid(): Boolean =
        (type.value == SyllabusType.WeightBased && items.value.sumOf {
            it.percentage.value ?: 0
        } == 100) ||
                type.value == SyllabusType.PointBased
}

data class UiSyllabusItem(
    val name: MutableState<String> = mutableStateOf(""),
    val percentage: MutableState<Int?> = mutableStateOf(null),
)

data class SyllabusItem(
    val name: String,
    val percentage: Int
) {
    fun toUiSyllabusItem() = UiSyllabusItem(
        name = mutableStateOf(name),
        percentage = mutableStateOf(percentage)
    )
}

val defaultSyllabus = Syllabus(
    items = listOf(
        SyllabusItem(name = "Homework", percentage = 20),
        SyllabusItem(name = "Quiz", percentage = 30),
        SyllabusItem(name = "Test", percentage = 50),
    )
)