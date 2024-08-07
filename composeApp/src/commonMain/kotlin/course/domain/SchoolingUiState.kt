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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.random.Random
import kotlin.time.Duration

data class TermUiState(
    val termName: MutableStateFlow<String>,
    val startDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null),
    val endDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null),
    val courses: MutableStateFlow<List<CourseUiState>> = MutableStateFlow(emptyList()),
    val existingTerm: Term? = null
) {
    val isValid = combine(
        termName, startDate, endDate, courses
    ) { name, startDate, endDate, courses ->
        name.isNotEmpty() && startDate != null && endDate != null && startDate < endDate && courses.isNotEmpty()
    }

    fun toTerm() = Term(
        id = existingTerm?.id ?: Random.nextInt(),
        name = termName.value,
        startDate = startDate.value,
        endDate = endDate.value,
        courses = courses.value.map { it.toCourse() }.toSet(),
    )
}

data class SchoolingUiState(
    val student: Student, val uiTerm: TermUiState
)

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
    val courseName: MutableStateFlow<String> = MutableStateFlow(""),
    val description: MutableStateFlow<String> = MutableStateFlow(""),
    val time: MutableStateFlow<LocalTime?> = MutableStateFlow(null),
    val duration: MutableStateFlow<Duration?> = MutableStateFlow(null),
    val interval: MutableStateFlow<UiClassInterval> = MutableStateFlow(UiClassInterval()),
    val color: MutableStateFlow<Int> = MutableStateFlow(red.toArgb()),
    val syllabus: MutableStateFlow<UiSyllabus> = MutableStateFlow(UiSyllabus()),
    val gradeScale: MutableStateFlow<GradeScale> = MutableStateFlow(defaultGradeScale()),
    val isNew: Boolean = false
) {
    private val syllabusValid: Flow<Boolean> = syllabus.transform { it.isValid }

    val isValid = combine(courseName, syllabusValid) { name, syllabusValid ->
        name.isNotBlank() && syllabusValid
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
    val type: MutableStateFlow<SyllabusType> = MutableStateFlow(SyllabusType.PointBased),
    val items: MutableStateFlow<List<UiSyllabusItem>> = MutableStateFlow(listOf(UiSyllabusItem()))
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val itemsValid = items.flatMapLatest { items ->
        combine(items.map { it.isValid }) { it.asList() }
    }

    val isValid = combine(type, items, itemsValid) { type, items, itemsValid ->
        itemsValid.all { it } && (type == SyllabusType.WeightBased && items.sumOf {
            it.percentage.value ?: 0
        } == 100) || type == SyllabusType.PointBased
    }

    fun toSyllabus() = Syllabus(type = type.value, items = items.value.map {
        it.toSyllabusItem()
    })
}

data class UiSyllabusItem(
    val name: MutableStateFlow<String> = MutableStateFlow(""),
    val percentage: MutableStateFlow<Int?> = MutableStateFlow(null),
) {
    val isValid = combine(name, percentage) { name, percentage ->
        name.isNotEmpty() && percentage?.let { it in 1..100 } == true
    }

    fun toSyllabusItem() = SyllabusItem(
        name = name.value, percentage = percentage.value ?: 0
    )
}

val defaultSyllabus = Syllabus(
    items = listOf(
        SyllabusItem(name = "Homework", percentage = 20),
        SyllabusItem(name = "Quiz", percentage = 30),
        SyllabusItem(name = "Test", percentage = 50),
    ), type = SyllabusType.WeightBased
)
