package course.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import common.AppIconButton
import common.ConfigurableInput
import common.FullScreenProgressIndicator
import common.HighlightBox
import course.domain.CourseUiState
import course.domain.CoursesViewModel
import course.domain.TermUiState
import kotlinx.coroutines.launch
import onboarding.presentation.OnboardingResult
import onboarding.presentation.onboardingStep.OnboardingStep
import org.koin.compose.koinInject

class CoursesSetupStep : OnboardingStep() {
    override val name = "Student Setup"
    override val contentCta = ""
    lateinit var coursesViewModel: CoursesViewModel

    override suspend fun evaluateContinue(): OnboardingResult {
        return OnboardingResult.Success
    }

    override fun canContinue() = coursesViewModel.isValid()

    @Composable
    override fun ColumnScope.OnboardingContent() {
        coursesViewModel = koinInject()
        val uiState = coursesViewModel.uiState.collectAsState()
        val coScope = rememberCoroutineScope()
        val bottomsheetNavigator = LocalBottomSheetNavigator.current

        LaunchedEffect(null) {
            coursesViewModel.initialize()
        }

        uiState.value?.let { state ->
            val currentTerm = state.studentTerms[state.selectedTerm.value]
            Text(
                text = "Lets get ${currentTerm.student.name} set up!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
            )

            Row(modifier = Modifier.padding(top = 24.dp)) {
                ConfigurableInput(
                    text = currentTerm.termName.value,
                    label = "Term",
                    onTextChange = {
                        currentTerm.termName.value = it
                    },
                    trailing = {
                        Row {
                            if (currentTerm.startDate.value == null) {
                                HighlightBox(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .padding(horizontal = 2.dp, vertical = 8.dp),
                                    text = "Set Dates",
                                    color = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                                    frontIcon = {
                                        Icon(
                                            rememberVectorPainter(Icons.Rounded.DateRange),
                                            null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        bottomsheetNavigator.show(TermDateBottomsheet(currentTerm) {
                                            coScope.launch {
                                                coursesViewModel.setDefaultDates()
                                            }
                                        })
                                    }
                                )
                            } else {
                                HighlightBox(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .padding(horizontal = 2.dp, vertical = 8.dp),
                                    text = currentTerm.startDate.value?.let { formatDate(it) },
                                    color = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                                    frontIcon = {
                                        Icon(
                                            rememberVectorPainter(Icons.Rounded.DateRange),
                                            null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        bottomsheetNavigator.show(TermDateBottomsheet(currentTerm) {
                                            coScope.launch {
                                                coursesViewModel.setDefaultDates()
                                            }
                                        })
                                    }
                                )
                                HighlightBox(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                        .padding(horizontal = 2.dp, vertical = 8.dp),
                                    text = currentTerm.endDate.value?.let { formatDate(it) },
                                    color = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                                    frontIcon = {
                                        Icon(
                                            rememberVectorPainter(Icons.Rounded.DateRange),
                                            null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        bottomsheetNavigator.show(TermDateBottomsheet(termUiState = currentTerm) {
                                            coScope.launch {
                                                coursesViewModel.setDefaultDates()
                                            }
                                        })
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                )
            }
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "Enter Classes",
                    style = MaterialTheme.typography.titleLarge,
                )
                Box(modifier = Modifier.weight(1f)) {
                    AppIconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = {
                        bottomsheetNavigator.show(CourseDetailsScreen())
                    })
                }
            }
            if (coursesViewModel.currentTerm?.courses?.value.isNullOrEmpty()) {
                FilledTonalButton(
                    modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
                    onClick = {
                        coScope.launch {
                            coursesViewModel.selectDefaultSyllabusForTerm()
                        }
                    },
                ) {
                    Text(
                        text = "Use default state requirements",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            CoursesTable(term = coursesViewModel.currentTerm ?: return)
        } ?: run {
            FullScreenProgressIndicator()
        }
    }

    @Composable
    fun ColumnScope.CoursesTable(modifier: Modifier = Modifier, term: TermUiState) {
        val courses = term.courses

        AnimatedVisibility(
            modifier = modifier.padding(top = 8.dp),
            visible = courses.value.isNotEmpty()
        ) {
            Column {
                Column(
                    modifier = Modifier.fillMaxWidth().background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp)
                    ).clip(RoundedCornerShape(16.dp))
                ) {
                    courses.value.forEach { course ->
                        CourseRow(course = course)
                    }
                }
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp),
                    textAlign = TextAlign.Center,
                    text = "Later we will help you to add or generate course content and schedules for ${coursesViewModel.currentTerm?.student?.name}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Thin)
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun CourseRow(modifier: Modifier = Modifier, course: CourseUiState) {
        val coursesViewModel: CoursesViewModel = koinInject()
        val navigator = LocalBottomSheetNavigator.current

        Row(
            modifier = modifier.fillMaxWidth().clickable {
                navigator.show(CourseDetailsScreen(course))
            }
        ) {
            Box(
                modifier = Modifier.width(24.dp).height(56.dp)
                    .background(color = Color(course.color.value))
            )
            FlowRow(modifier = Modifier.padding(horizontal = 16.dp).weight(1f)) {
                Text(modifier = Modifier.padding(vertical = 16.dp), text = course.courseName.value)
            }
            AppIconButton(
                modifier = Modifier.padding(end = 8.dp).align(Alignment.CenterVertically),
                painter = rememberVectorPainter(Icons.Rounded.Close),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error,
                onClick = {
                    coursesViewModel.removeCourse(course)
                }
            )
        }
    }
}
