package state.data

import androidx.compose.ui.graphics.toArgb
import common.blue
import common.green
import common.purple
import common.red
import course.data.ClassInterval
import course.data.Course
import course.data.defaultGradeScale
import course.domain.defaultSyllabus
import family.data.Family
import family.data.Student

class DemoStateDataSource : StateDataSource {

    // TODO replace Family with location once location work is started
    override suspend fun getRequiredCourses(family: Family, student: Student): Set<Course> {
        return demoCourses
    }
}

val demoCourses = setOf(
    Course(
        name = "Math",
        syllabus = defaultSyllabus,
        color = red.toArgb(),
        interval = ClassInterval.weekDays(),
        gradeScale = defaultGradeScale(),
        assignments = emptyList(),
    ),
    Course(
        name = "English",
        syllabus = defaultSyllabus,
        color = blue.toArgb(),
        interval = ClassInterval.weekDays(),
        gradeScale = defaultGradeScale(),
        assignments = emptyList(),
    ),
    Course(
        name = "Science",
        syllabus = defaultSyllabus,
        color = green.toArgb(),
        interval = ClassInterval.weekDays(),
        gradeScale = defaultGradeScale(),
        assignments = emptyList(),
    ),
    Course(
        name = "Physical Education",
        syllabus = defaultSyllabus,
        color = purple.toArgb(),
        interval = ClassInterval.weekDays(),
        gradeScale = defaultGradeScale(),
        assignments = emptyList(),
    ),
)

interface StateDataSource {
    suspend fun getRequiredCourses(family: Family, student: Student): Set<Course>
}
