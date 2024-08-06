package family.domain

import family.data.Family
import family.data.Student
import family.data.StudentGrade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val studentsValid = students.flatMapLatest { students ->
        combine(students.map { it.isValid }) { it.asList() }
    }

    val isValid = combine(familyName, city, students, studentsValid) { familyName, city, students, valid ->
        familyName.isNotBlank() && city.isNotBlank() && students.isNotEmpty() && valid.all { it }
    }
}

data class StudentInput(
    val name: MutableStateFlow<String?> = MutableStateFlow(null),
    val gradeLevel: MutableStateFlow<StudentGrade?> = MutableStateFlow(null),
) {
    val isValid = combine(name, gradeLevel) { name, gradeLevel ->
        !name.isNullOrBlank() && gradeLevel != null
    }

    fun toStudent(): Student =
        Student(name = name.value ?: "", grade = gradeLevel.value ?: StudentGrade.First)
}
