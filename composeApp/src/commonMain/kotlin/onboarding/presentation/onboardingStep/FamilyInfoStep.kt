package onboarding.presentation.onboardingStep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Grade
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.materialkolor.ktx.harmonize
import common.AppIconButton
import common.FullScreenProgressIndicator
import common.HighlightBox
import homely.composeapp.generated.resources.Res
import kotlinx.datetime.Instant
import onboarding.data.DemoOnboardingDataSource
import onboarding.data.OnboardingDataSource
import onboarding.data.OnboardingRepository
import onboarding.domain.OnboardingViewModel
import onboarding.presentation.OnboardingResult
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import org.koin.dsl.module
import kotlin.time.Duration

data class StudentInput(
    val name: MutableState<String?> = mutableStateOf(null),
    val gradeLevel: MutableState<StudentGrade?> = mutableStateOf(null),
) {
    fun isValid(): Boolean = !name.value.isNullOrBlank() && gradeLevel.value != null
    fun toStudent(): Student =
        Student(name = name.value ?: "", grade = gradeLevel.value ?: StudentGrade.First)
}

enum class StudentGrade {
    PreK, Kindergarten, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth, Ninth, Tenth, Eleventh, Twelfth
}

enum class CourseGrade(val display: String) {
    A("A"), AMinus("A-"), BPlus("B+"), B("B"), BMinus("B-"), CPlus("C+"), C("C"), CMinus("C-"), DPlus(
        "D+"
    ),
    D("D"), DMinus(
        "D-"
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
    val allTerms: List<Term>? = null,
    val activeTerm: Term? = null,
) {
    fun toStudentInput() = StudentInput(
        name = mutableStateOf(name),
        gradeLevel = mutableStateOf(grade),
    )
}

data class Family(
    val familyName: String,
    val city: String,
    val students: List<Student>
) {
    fun toFamilyUiState() = FamilyUiState(
        familyName = mutableStateOf(familyName),
        city = mutableStateOf(city),
        students = mutableStateOf(students.map { it.toStudentInput() }),
    )
}

interface FamilyDataSource {
    suspend fun putFamily(family: Family)
    suspend fun getFamily(): Family?
}

class DemoFamilyDataSource(var existingFamily: Family? = null) : FamilyDataSource {
    override suspend fun putFamily(family: Family) {
        existingFamily = family
    }

    override suspend fun getFamily(): Family? = existingFamily
}

class FamilyRepository(val familyDataSource: FamilyDataSource) {
    val currentFamily: Family? get() = _currentFamily
    private var _currentFamily: Family? = null

    suspend fun initialize() {
        if (_currentFamily == null) {
            _currentFamily = familyDataSource.getFamily()
        }
    }

    suspend fun update(family: Family) {
        familyDataSource.putFamily(family)
        _currentFamily = family
    }
}

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
}


class FamilyInfoViewModel(private val familyRepository: FamilyRepository) : ScreenModel {
    val familyUiState = mutableStateOf<FamilyUiState?>(null)

    suspend fun initialize() {
        familyRepository.initialize()
        familyUiState.value = familyRepository.currentFamily?.toFamilyUiState() ?: FamilyUiState()
    }

    fun canContinue(): OnboardingResult {
        val family = familyUiState.value
            ?: return OnboardingResult.Failure(message = "Unexpected Error Occurred, try again")

        return if (family.familyName.value.isNotBlank() && family.city.value.isNotBlank() && family.students.value.all { it.isValid() }) {
            OnboardingResult.Success
        } else {
            OnboardingResult.Failure(
                message = if (family.familyName.value.isBlank()) {
                    "Please enter your family name"
                } else if (family.city.value.isBlank()) {
                    "The city entered is invalid or missing"
                } else {
                    "Please fill in all student fields"
                }
            )
        }
    }
}

val familyModule = module {
    single<FamilyDataSource> { DemoFamilyDataSource() }
    single { FamilyRepository(get()) }
    single { FamilyInfoViewModel(get()) }
}

class FamilyInfo : OnboardingStep() {
    override val name = "Family"
    override val contentCta = "Set up your students"

    private lateinit var familyViewModel: FamilyInfoViewModel

    @Composable
    override fun ColumnScope.OnboardingContent() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        familyViewModel = koinInject()

        LaunchedEffect(null) {
            familyViewModel.initialize()
        }

        familyViewModel.familyUiState.value?.let { family ->
            Row {
                Column(modifier = Modifier.weight(.5f)) {
                    Text(text = "Family Name")
                }
                OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                    value = family.familyName.value,
                    onValueChange = { family.familyName.value = it })
            }

            // Future use google places api
            // https://stackoverflow.com/questions/70834787/implementing-google-places-autocomplete-textfield-implementation-in-jetpack-comp/72586090#72586090
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Row(Modifier.weight(.5f)) {
                    Text(modifier = Modifier.align(Alignment.CenterVertically), text = "City")
                    TextButton(onClick = {
                        bottomSheetNavigator.show(ZipcodeBottomsheet())
                    }) {
                        Text("Why?")
                    }
                }
                OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                    value = family.city.value,
                    onValueChange = { family.city.value = it })
            }
            Column(Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Row {
                    Text(
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        text = "Students",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    AppIconButton(modifier = Modifier, onClick = {
                        family.students.value =
                            family.students.value.toMutableList().also { it.add(StudentInput()) }
                    })
                }
                family.students.value.forEachIndexed { index, student ->
                    StudentRow(index = index, student = student, remove = { studentInput ->
                        family.students.value =
                            family.students.value.toMutableList().also { it.remove(studentInput) }
                    })
                }
            }
        } ?: run {
            FullScreenProgressIndicator()
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
        var dialogShowing by remember { mutableStateOf(false) }

        AnimatedVisibility(
            visibleState = state,
        ) {
            Row(modifier = modifier.padding(top = 4.dp)) {
                OutlinedTextField(modifier = Modifier.weight(.8f),
                    value = student.name.value ?: "",
                    label = { Text("Name") },
                    onValueChange = { student.name.value = it },
                    shape = CircleShape,
                    trailingIcon = {
                        HighlightBox(
                            modifier = Modifier.align(Alignment.CenterVertically)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            text = student.gradeLevel.value?.name ?: "Select Grade",
                            color = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                            frontIcon = {
                                Icon(
                                    rememberVectorPainter(Icons.Rounded.School),
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                dialogShowing = true
                            })
                    })

                if (dialogShowing) {
                    var gradeLevel by mutableStateOf(student.gradeLevel.value)
                    common.AlertDialogWithContent(
                        title = "Select students grade level",
                        onClose = {
                            student.gradeLevel.value = gradeLevel
                            dialogShowing = false
                        },
                        content = {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                StudentGrade.entries.forEach {
                                    Row(
                                        Modifier
                                            .clickable { gradeLevel = it }
                                            .background(
                                                color = if (gradeLevel == it) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            text = it.name,
                                            color = if (gradeLevel == it) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

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