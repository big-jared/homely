package family.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import family.data.Family
import family.data.Student
import family.data.StudentGrade

data class FamilyUiState(
    var familyName: MutableState<String> = mutableStateOf(""),
    var city: MutableState<String> = mutableStateOf(""),
    var students: MutableState<List<StudentInput>> = mutableStateOf(listOf(StudentInput()))
) {
    fun toFamily(): Family = Family(
        familyName = familyName.value,
        city = city.value,
        students = students.value.map { it.toStudent() }
    )

    fun isValid() = familyName.value.isNotBlank() && city.value.isNotBlank() && students.value.all { it.isValid() } && students.value.isNotEmpty()
}

data class StudentInput(
    val name: MutableState<String?> = mutableStateOf(null),
    val gradeLevel: MutableState<StudentGrade?> = mutableStateOf(null),
) {
    fun isValid(): Boolean = !name.value.isNullOrBlank() && gradeLevel.value != null
    fun toStudent(): Student =
        Student(name = name.value ?: "", grade = gradeLevel.value ?: StudentGrade.First)
}
