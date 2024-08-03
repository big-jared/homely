package state.data

import course.data.Course
import family.data.Family
import family.data.Student

class StateRepository(val dataSource: StateDataSource) {
    suspend fun getRequiredCourses(family: Family, student: Student): List<Course> {
        return dataSource.getRequiredCourses(family, student)
    }

//    suspend fun nationalHolidays(family: Family, student: Student): List<Holiday> = listOf(
//        Holiday("New Years", startDate = LocalDate())
//    )
}

// data class Holiday(
//    val name: String,
//    val startDate: LocalDate,
//    val endDate: LocalDate,
// )
//
// data class RelativeLocalDate()
