package course.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import common.AppIconButton
import common.blue
import common.green
import common.lightBlue
import common.lightGreen
import common.lightNavy
import common.lightOrange
import common.lightPurple
import common.lightRed
import common.lightYellow
import common.navy
import common.orange
import common.purple
import common.red
import common.yellow
import course.domain.CourseUiState
import course.domain.CoursesViewModel
import course.domain.SyllabusType
import course.domain.UiSyllabusItem
import course.domain.defaultSyllabus
import org.koin.compose.koinInject


class AddCourseScreen(val course: CourseUiState = CourseUiState()) : Screen {
    private lateinit var viewModel: CoursesViewModel

    @Composable
    override fun Content() {
        viewModel = koinInject()
        val navigator = LocalBottomSheetNavigator.current
        val horizontalModifier = Modifier.padding(horizontal = 16.dp)
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            CourseHeader(horizontalModifier)
            ClassNameRow(horizontalModifier)
            ColorRow()
            SyllabusTable(horizontalModifier)

            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                onClick = {
                    viewModel.addCourse(course)
                    navigator.hide()
                },
                enabled = course.isValid()
            ) {
                Text("Create")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SyllabusTable(modifier: Modifier = Modifier) {
        val syllabus = course.syllabus.value

        Column(
            modifier = modifier.padding(top = 16.dp).background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                SegmentedButton(syllabus.type.value == SyllabusType.PointBased,
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    onClick = { syllabus.type.value = SyllabusType.PointBased },
                    label = {
                        Text("Point Based")
                    })
                SegmentedButton(syllabus.type.value == SyllabusType.WeightBased,
                    onClick = { syllabus.type.value = SyllabusType.WeightBased },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = {
                        Text("Weight Based")
                    })
            }

            when (syllabus.type.value) {
                SyllabusType.PointBased -> {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp),
                        text = "Overall grade is calculated as a total of points received / points possible",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                SyllabusType.WeightBased -> {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp),
                        text = "Overall grade is calculated as a sum of weighted categories listed below",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )

                    FilledTonalButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp), onClick = {
                            course.syllabus.value = defaultSyllabus.toUiSyllabus()
                        }) {
                        Text("Use defaults")
                    }

                    syllabus.items.value.forEachIndexed { index, syllabusItem ->
                        val state = remember {
                            MutableTransitionState(false).apply {
                                targetState = true
                            }
                        }

                        AnimatedVisibility(
                            visibleState = state,
                        ) {
                            Row(
                                modifier = Modifier.padding(bottom = 16.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.weight(.55f),
                                    value = syllabusItem.name.value,
                                    onValueChange = { syllabusItem.name.value = it },
                                    label = { Text("Category") },
                                    placeholder = { Text("Homework") })
                                val weight = syllabusItem.percentage.value
                                OutlinedTextField(
                                    modifier = Modifier.padding(start = 16.dp)
                                        .weight(.3f),
                                    value = if (weight != null) "$weight%" else "",
                                    onValueChange = {
                                        val percentage = it.removeSuffix("%").toIntOrNull()
                                            ?.takeIf { value -> value in 0..100 }
                                        syllabusItem.percentage.value = percentage
                                    },
                                    label = { Text("%") },
                                    placeholder = { Text("20%") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Box(
                                    modifier = Modifier.weight(.15f).padding(start = 8.dp)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    AppIconButton(
                                        modifier = Modifier.align(Alignment.Center),
                                        onClick = {
                                            syllabus.items.value =
                                                syllabus.items.value.toMutableList()
                                                    .also { it.remove(syllabusItem) }
                                        },
                                        painter = rememberVectorPainter(Icons.Rounded.Close),
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp),
                        text = "Total ${syllabus.items.value.sumOf { it.percentage.value ?: 0 }}%",
                        color = if (syllabus.isValid()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp),
                        onClick = {
                            syllabus.items.value = syllabus.items.value.toMutableList()
                                .also { it.add(UiSyllabusItem()) }
                        },
                    ) {
                        Text("Add category")
                    }
                }
            }
        }
    }

    @Composable
    fun CourseHeader(modifier: Modifier = Modifier) {
        val navigator = LocalBottomSheetNavigator.current
        Row(modifier = modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                text = "Add Class",
                style = MaterialTheme.typography.headlineSmall,
            )

            AppIconButton(painter = rememberVectorPainter(Icons.Rounded.Close), onClick = {
                navigator.hide()
            })
        }
    }

    @Composable
    fun ClassNameRow(modifier: Modifier = Modifier) {
        Row(modifier = modifier.padding(top = 24.dp)) {
            Column(modifier = Modifier.weight(.5f)) {
                Text(text = "Class name")
            }
            OutlinedTextField(modifier = Modifier.padding(start = 16.dp).weight(.5f),
                value = course.courseName.value,
                onValueChange = { course.courseName.value = it })
        }
    }

    @Composable
    fun ColorRow(modifier: Modifier = Modifier) {
        var itemPosition by remember { mutableStateOf(0f) }
        val scrollState = rememberScrollState()

        LaunchedEffect(key1 = null) {
            scrollState.animateScrollTo(maxOf(0, itemPosition.toInt()))
        }

        Text(modifier = modifier.padding(top = 16.dp).padding(horizontal = 16.dp), text = "Color")
        Row(
            modifier = Modifier.padding(top = 16.dp).height(84.dp).horizontalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            listOf(
                red,
                lightRed,
                orange,
                lightOrange,
                yellow,
                lightYellow,
                blue,
                lightBlue,
                green,
                lightGreen,
                purple,
                lightPurple,
                navy,
                lightNavy
            ).forEach { color ->
                val selected = course.color.value == color.toArgb()
                ColorHintCircle(modifier = Modifier.size(animateDpAsState(if (selected) 84.dp else 64.dp).value)
                    .then(if (selected) Modifier.onGloballyPositioned { layoutCoordinates ->
                        itemPosition =
                            layoutCoordinates.positionInRoot().x - layoutCoordinates.size.width
                    } else Modifier).padding(8.dp).clip(CircleShape)
                    .clickable { course.color.value = color.toArgb() }
                    .align(Alignment.CenterVertically), color)
            }
        }
    }
}

@Composable
fun ColorHintCircle(modifier: Modifier = Modifier, color: Color) {
    Box(modifier.background(color = color, shape = CircleShape))
}
