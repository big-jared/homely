package family.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.materialkolor.ktx.harmonize
import common.AppIconButton
import common.ConfigurableInput
import common.FullScreenProgressIndicator
import common.HighlightBox
import family.data.StudentGrade
import family.domain.FamilyInfoViewModel
import family.domain.FamilyUiState
import family.domain.StudentInput
import onboarding.presentation.OnboardingResult
import onboarding.presentation.onboardingStep.OnboardingStep
import org.koin.compose.koinInject

class FamilyInfo : OnboardingStep() {
    override val name = "Family"
    override val contentCta = ""

    private lateinit var familyViewModel: FamilyInfoViewModel

    @Composable
    override fun ColumnScope.OnboardingContent() {
        familyViewModel = koinInject()
        LaunchedEffect(null) {
            familyViewModel.initialize()
        }

        familyViewModel.familyUiState.value?.let { family ->
            canContinue.value = family.isValid.collectAsState(false).value
            FamilyInfo(family = family)
            StudentTable(family = family)
        } ?: run {
            FullScreenProgressIndicator()
        }
    }

    override suspend fun evaluateContinue(): OnboardingResult {
        return try {
            familyViewModel.update()
            OnboardingResult.Success
        } catch (e: Exception) {
            OnboardingResult.Failure()
        }
    }

    @Composable
    fun FamilyInfo(modifier: Modifier = Modifier, family: FamilyUiState) {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Row(modifier) {
            Column(modifier = Modifier.weight(.5f)) {
                Text(text = "Family Name")
            }
            OutlinedTextField(
                modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = family.familyName.collectAsState().value,
                onValueChange = { family.familyName.value = it },
                shape = RoundedCornerShape(16.dp)
            )
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
            OutlinedTextField(
                modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = family.city.collectAsState().value,
                onValueChange = { family.city.value = it },
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    AppIconButton(
                        modifier = Modifier.size(32.dp),
                        painter = rememberVectorPainter(Icons.Rounded.LocationOn),
                        onClick = {
                            family.city.value = "Boise"
                        }
                    )
                }
            )
        }

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Row(Modifier.weight(.5f)) {
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(text = "Holidays")
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "Federal holidays are added by default",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light)
                    )
                }
            }
            Button(modifier = Modifier.padding(start = 16.dp).weight(.5f), onClick = {
                bottomSheetNavigator.show(ZipcodeBottomsheet())
            }) {
                Text("Customize")
            }
        }
    }

    @Composable
    fun StudentTable(modifier: Modifier = Modifier, family: FamilyUiState) {
        Column(modifier.fillMaxWidth().padding(top = 24.dp)) {
            Row {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                        .padding(top = 16.dp, start = 8.dp),
                    text = "Students",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
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
                ConfigurableInput(
                    text = student.name.collectAsState().value ?: "",
                    onTextChange = { student.name.value = it },
                    label = "Name",
                    trailing = {
                        Row {
                            HighlightBox(
                                modifier = Modifier.align(Alignment.CenterVertically)
                                    .padding(horizontal = 2.dp, vertical = 8.dp),
                                text = student.gradeLevel.collectAsState().value?.name ?: "Select Grade",
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
                                }
                            )

                            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                                AppIconButton(
                                    modifier = Modifier.align(Alignment.Center)
                                        .padding(horizontal = 4.dp)
                                        .padding(end = 2.dp).size(28.dp),
                                    painter = rememberVectorPainter(Icons.Rounded.Close),
                                    containerColor = MaterialTheme.colorScheme.errorContainer.harmonize(
                                        MaterialTheme.colorScheme.primary
                                    ),
                                    contentColor = MaterialTheme.colorScheme.error.harmonize(
                                        MaterialTheme.colorScheme.primary
                                    ),
                                    onClick = {
                                        remove(student)
                                    },
                                )
                            }
                        }
                    }
                )

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
