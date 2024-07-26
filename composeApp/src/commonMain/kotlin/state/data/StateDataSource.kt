package state.data

import androidx.compose.ui.graphics.toArgb
import common.blue
import common.green
import common.purple
import common.red
import course.domain.Course
import course.domain.defaultSyllabus
import family.data.Family
import family.data.Student

class DemoStateDataSource: StateDataSource {

    override suspend fun getRequiredCourses(family: Family, student: Student): List<Course> {
        return listOf(
            Course(
                "Math",
                defaultSyllabus,
                red.toArgb()
            ),
            Course(
                "English",
                defaultSyllabus,
                blue.toArgb()
            ),
            Course(
                "Science",
                defaultSyllabus,
                green.toArgb()
            ),
            Course(
                "Physical Education",
                defaultSyllabus,
                purple.toArgb()
            ),
        )
    }
}

interface StateDataSource {
    suspend fun getRequiredCourses(family: Family, student: Student): List<Course>
}