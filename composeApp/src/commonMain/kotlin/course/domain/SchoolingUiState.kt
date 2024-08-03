package course.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import common.red
import course.data.ClassInterval
import course.data.Course
import course.data.GradeScale
import course.data.Syllabus
import course.data.SyllabusItem
import course.data.SyllabusType
import course.data.Term
import course.data.defaultGradeScale
import family.data.Student
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Duration

data class TermUiState(
    val termName: MutableState<String> = mutableStateOf("${student.grade.name} Grade"),
    val startDate: MutableState<LocalDate?> = mutableStateOf(null),
    val endDate: MutableState<LocalDate?> = mutableStateOf(null),
    val courses: MutableState<List<CourseUiState>> = mutableStateOf(emptyList()),
) {
    fun isValid() = this.courses.value.all { it.isValid() } &&
            this.startDate.value != null &&
            this.endDate.value != null &&
            this.termName.value.isNotEmpty()

    fun toTerm() = Term(
        name = termName.value,
        startDate = startDate.value ,
        endDate = startDate.value,
        courses = courses.value.map { it.toCourse() }.toSet(),
    )
}

data class SchoolingUiState(
    val student: Student,
    val uiTerm: TermUiState
) {
    fun isValid() = uiTerm.isValid()
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

    fun toInterval() = ClassInterval(
        monday = monday.value,
        tuesday = tuesday.value,
        wednesday = wednesday.value,
        thursday = thursday.value,
        friday = friday.value,
        saturday = saturday.value,
        sunday = sunday.value
    )
}

data class CourseUiState(
    val courseName: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val time: MutableState<LocalTime?> = mutableStateOf(null),
    val duration: MutableState<Duration?> = mutableStateOf(null),
    val interval: MutableState<UiClassInterval> = mutableStateOf(UiClassInterval()),
    val color: MutableState<Int> = mutableStateOf(red.toArgb()),
    val syllabus: MutableState<UiSyllabus> = mutableStateOf(UiSyllabus()),
    val gradeScale: MutableState<GradeScale> = mutableStateOf(defaultGradeScale()),
    val isNew: Boolean = false
) {
    fun isValid(): Boolean {
        return courseName.value.isNotBlank() && syllabus.value.isValid()
    }

    fun toCourse() = Course(
        name = courseName.value,
        description = description.value,
        time = time.value,
        duration = duration.value,
        interval = interval.value.toInterval(),
        color = color.value,
        syllabus = syllabus.value.toSyllabus(),
        assignments = emptyList(),
        gradeScale = gradeScale.value
    )
}

data class UiSyllabus(
    val type: MutableState<SyllabusType> = mutableStateOf(SyllabusType.PointBased),
    val items: MutableState<List<UiSyllabusItem>> = mutableStateOf(listOf(UiSyllabusItem()))
) {
    fun isValid(): Boolean = (type.value == SyllabusType.WeightBased && items.value.sumOf {
        it.percentage.value ?: 0
    } == 100) || type.value == SyllabusType.PointBased

    fun toSyllabus() = Syllabus(
        type = type.value,
        items = items.value.map {
            it.toSyllabusItem()
        }
    )
}

data class UiSyllabusItem(
    val name: MutableState<String> = mutableStateOf(""),
    val percentage: MutableState<Int?> = mutableStateOf(null),
) {
    fun toSyllabusItem() = SyllabusItem(
        name = name.value,
        percentage = percentage.value ?: 0
    )
}

val defaultSyllabus = Syllabus(
    items = listOf(
        SyllabusItem(name = "Homework", percentage = 20),
        SyllabusItem(name = "Quiz", percentage = 30),
        SyllabusItem(name = "Test", percentage = 50),
    ),
    type = SyllabusType.WeightBased
)
