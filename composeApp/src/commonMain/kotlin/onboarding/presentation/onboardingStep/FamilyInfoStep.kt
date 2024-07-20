package onboarding.presentation.onboardingStep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.materialkolor.ktx.harmonize
import common.AppIconButton
import kotlinx.datetime.Instant
import onboarding.presentation.OnboardingResult
import onboarding.presentation.isHorizontalLayout
import kotlin.time.Duration

data class StudentInput(
    val name: MutableState<String?> = mutableStateOf(null),
    val gradeLevel: MutableState<Int?> = mutableStateOf(null),
) {
    fun isValid(): Boolean = !name.value.isNullOrBlank() && (gradeLevel.value ?: 0) > 0
}

enum class StudentGrade {
    First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Nineth, Tenth, Eleventh, Twelfth
}

enum class CourseGrade(val display: String) {
    A("A"), AMinus("A-"), BPlus("B+"), B("B"), BMinus("B-"), CPlus("C+"), C("C"), CMinus("C-"), DPlus("D+"), D("D"), DMinus( "D-"
    ),
    F("F")
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

data class Assignment(
    val name: String,
    val received: Int? = null,
    val possible: Int,
    val notes: String,
    val dueDate: Instant? = null,
    val duration: Duration? = null,
)

data class GradeCategory(
    val name: String,
    val weight: Double,
    val assignments: List<Assignment>
)

data class Course(
    val name: String,
    val color: Int,
    val scale: GradeScale,
    val categories: List<GradeCategory>
)

data class Term(
    val name: String,
    val courses: List<Course>,
)

data class Student(
    val name: String,
    val grade: StudentGrade,
    val terms: List<Term>,
    val activeTerm: Term,
)

data class Family(
    val familyName: String,
    val city: String,
    val students: List<Student>
)

class FamilyRepository {

}

class FamilyInfoViewModel : ScreenModel {
    var familyName by mutableStateOf("")
    var city by mutableStateOf("")
    var students by mutableStateOf(listOf(StudentInput()))
    var gradeScale by mutableStateOf(defaultGradeScale())

    fun canContinue() = if (familyName.isNotBlank() && city.isNotBlank() && students.all { it.isValid() }) {
        OnboardingResult.Success
    } else {
        OnboardingResult.Failure(
            message = if (familyName.isBlank()) {
                "Please enter your family name"
            } else if (city.isBlank()) {
                "The city entered is invalid or missing"
            } else {
                "Please fill in all student fields"
            }
        )
    }
}

class FamilyInfo : OnboardingStep() {
    override val name = "Courses"
    override val contentCta = "Set up your students"

    private val familyViewModel = FamilyInfoViewModel()

    @Composable
    override fun ColumnScope.OnboardingContent() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        Row {
            Column(modifier = Modifier.weight(.5f)) {
                Text(text = "Family Name")
            }
            OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = familyViewModel.familyName,
                onValueChange = { familyViewModel.familyName = it })
        }

        // Future use google places api
        // https://stackoverflow.com/questions/70834787/implementing-google-places-autocomplete-textfield-implementation-in-jetpack-comp/72586090#72586090
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Row(Modifier.weight(.5f)) {
                Text(modifier = Modifier.align(Alignment.CenterVertically), text = "city")
                TextButton(onClick = {
                    bottomSheetNavigator.show(ZipcodeBottomsheet())
                }) {
                    Text("Why?")
                }
            }
            OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = familyViewModel.city,
                onValueChange = { familyViewModel.city = it })
        }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Row(Modifier.weight(.5f)) {
                Text(modifier = Modifier.align(Alignment.CenterVertically), text = "Grading Scale")
                TextButton(onClick = {
                    bottomSheetNavigator.show(ZipcodeBottomsheet())
                }) {
                    Text("What's this?")
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(40.dp).padding(all = 16.dp).drawWithContent {
                drawIntoCanvas {
                    drawLine(start = Offset(0f, 0f), end =  Offset(size.width, 0f), color = Color.Black)
                    familyViewModel.gradeScale
                    drawLine(start = Offset(0f, 0f), end =  Offset(size.width, 0f), color = Color.Black)

                }
            })

            OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = familyViewModel.city,
                onValueChange = { familyViewModel.city = it })
        }
        Column(Modifier.fillMaxWidth().padding(top = 24.dp)) {
            Row {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    text = "Students",
                    style = MaterialTheme.typography.titleLarge,
                )
                AppIconButton(modifier = Modifier, onClick = {
                    familyViewModel.students = familyViewModel.students.toMutableList().also { it.add(StudentInput()) }
                })
            }
            familyViewModel.students.forEachIndexed { index, student ->
                StudentRow(index = index, student = student, remove = { studentInput ->
                    familyViewModel.students = familyViewModel.students.toMutableList().also { it.remove(studentInput) }
                })
            }
        }
    }

    override suspend fun evaluateContinue(): OnboardingResult {
        return familyViewModel.canContinue()
    }

    @Composable
    fun ColumnScope.StudentRow(
        modifier: Modifier = Modifier,
        index: Int,
        student: StudentInput,
        remove: (StudentInput) -> Unit
    ) {
        val state = remember {
            MutableTransitionState(index == 0).apply {
                targetState = true
            }
        }

        AnimatedVisibility(
            visibleState = state,
        ) {
            Row(modifier = modifier.padding(top = 4.dp)) {
                OutlinedTextField(modifier = Modifier.weight(.5f),
                    value = student.name.value ?: "",
                    label = { Text("Name") },
                    onValueChange = { student.name.value = it })
                OutlinedTextField(modifier = Modifier.padding(start = 16.dp)
                    .weight(if (index == 0) .5f else .3f),
                    value = student.gradeLevel.value?.toString() ?: "",
                    label = { Text(if (index == 0) "Grade Level" else "Grade") },
                    onValueChange = { student.gradeLevel.value = it.toIntOrNull() })
                if (index != 0) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 4.dp)
                            .weight(.2f)
                    ) {
                        AppIconButton(modifier = Modifier.align(Alignment.Center),
                            painter = rememberVectorPainter(Icons.Rounded.Close),
                            containerColor = MaterialTheme.colorScheme.errorContainer.harmonize(
                                MaterialTheme.colorScheme.primary
                            ),
                            contentColor = MaterialTheme.colorScheme.error.harmonize(
                                MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                remove(student)
                            })
                    }
                }
            }
        }
    }
}

class ZipcodeBottomsheet() : Screen {
    @Composable
    override fun Content() {
        Column(Modifier.padding(16.dp).padding(bottom = 32.dp)) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "Why zipcode?",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Homely contains features that use location. Community, and state registration specifically. To better enable your experince please either grant location access to the app, or enter your zip code manually."
            )
        }
    }
}