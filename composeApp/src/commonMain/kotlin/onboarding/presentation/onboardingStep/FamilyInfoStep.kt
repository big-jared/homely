package onboarding.presentation.onboardingStep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.materialkolor.ktx.harmonize
import common.AppIconButton
import onboarding.presentation.OnboardingResult

data class StudentInput(
    val name: MutableState<String?> = mutableStateOf(null),
    val gradeLevel: MutableState<Int?> = mutableStateOf(null),
) {
    fun isValid(): Boolean = !name.value.isNullOrBlank() && (gradeLevel.value ?: 0) > 0
}

class FamilyInfoViewModel: ScreenModel {
    var familyName by mutableStateOf("")
    var zipcode by mutableStateOf("")
    var students by mutableStateOf(listOf(StudentInput()))

    fun canContinue() = if (familyName.isNotBlank() && zipcode.isNotBlank() && students.all { it.isValid() }) {
        OnboardingResult.Success
    } else {
        OnboardingResult.Failure(
            message = if (familyName.isBlank()) {
                "Please enter your family name"
            } else if (zipcode.isBlank()) {
                "The zipcode entered is incorrect or missing"
            } else {
                "Please fill in all student fields"
            }
        )
    }
}

class FamilyInfo : OnboardingStep() {
    override val name = "Family Info"
    override val contentCta = "Setup your family"

    private val familyViewModel = FamilyInfoViewModel()

    @Composable
    override fun ColumnScope.OnboardingContent() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        Row {
            Column(Modifier.weight(.5f)) {
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
                Text(modifier = Modifier.align(Alignment.CenterVertically), text = "Zipcode")
                TextButton(onClick = {
                    bottomSheetNavigator.show(ZipcodeBottomsheet())
                }) {
                    Text("Why?")
                }
            }
            OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = familyViewModel.zipcode,
                onValueChange = { familyViewModel.zipcode = it })
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