package family.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import family.data.Family
import family.data.Student
import family.data.StudentGrade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

data class FamilyUiState(
    var familyName: MutableStateFlow<String> = MutableStateFlow(""),
    var city: MutableStateFlow<String> = MutableStateFlow(""),
    var students: MutableStateFlow<List<StudentInput>> = MutableStateFlow(listOf(StudentInput())),
    val defaultStart: LocalDate? = null,
    val defaultEnd: LocalDate? = null,
) {
    fun toFamily(): Family = Family(
        familyName = familyName.value,
        city = city.value,
        students = students.value.map { it.toStudent() },
        defaultStart = defaultStart,
        defaultEnd = defaultEnd
    )

    val isValid = combine(familyName, city, students) { familyName, city, students ->
        familyName.isNotBlank() && city.isNotBlank() && students.all {
            it.isValid()
        } && students.isNotEmpty()
    }
}

data class StudentInput(
    val name: MutableStateFlow<String?> = MutableStateFlow(null),
    val gradeLevel: MutableStateFlow<StudentGrade?> = MutableStateFlow(null),
) {
    fun isValid(): Boolean = !name.value.isNullOrBlank() && gradeLevel.value != null
    fun toStudent(): Student =
        Student(name = name.value ?: "", grade = gradeLevel.value ?: StudentGrade.First)
}
