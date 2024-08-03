package course.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import common.HighlightBox
import course.domain.TermUiState
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDate(localDate: LocalDate): String {
    val month = localDate.month.name.lowercase().capitalize(Locale.current).take(3)
    val day = localDate.dayOfMonth + 1
    return if (localDate == now().toLocalDateTime(TimeZone.currentSystemDefault()).date) "Today" else "$month $day"
}

class TermDateBottomsheet(val termUiState: TermUiState, val saveAsDefaults: () -> Unit): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalBottomSheetNavigator.current
        val initialStartMs =
            termUiState.startDate.value?.let { (it.toEpochDays() + 1) * 24L * 60 * 60 * 1000 }
        val initialEndMs =
            termUiState.endDate.value?.let { (it.toEpochDays() + 1) * 24L * 60 * 60 * 1000 }

        val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = initialStartMs)
        val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEndMs)

        // When picker state changes, a recompose will happen. That recompose will trigger
        // state to be updated, effecting the caller of this bottomsheet
        run {
            termUiState.startDate.value = Instant.fromEpochMilliseconds(
                startDatePickerState.selectedDateMillis ?: return@run
            ).toLocalDateTime(
                TimeZone.currentSystemDefault()
            ).date

            termUiState.endDate.value = Instant.fromEpochMilliseconds(
                endDatePickerState.selectedDateMillis ?: return@run
            ).toLocalDateTime(
                TimeZone.currentSystemDefault()
            ).date
        }

        var selectingStart by remember { mutableStateOf<Boolean?>(null) }

        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    text = "Start Date"
                )
                HighlightBox(
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                    text = termUiState.startDate.value?.let { formatDate(it) } ?: "Set start",
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
                        selectingStart = true
                    }
                )
            }
            AnimatedVisibility(visible = selectingStart == true) {
                Column {
                    DatePicker(
                        state = startDatePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false
                    )
                }
            }
            Row {
                Text(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    text = "End Date"
                )
                HighlightBox(
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                    text = termUiState.endDate.value?.let { formatDate(it) } ?: "Set end",
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
                        selectingStart = false
                    }
                )
            }
            AnimatedVisibility(visible = selectingStart == false) {
                Column {
                    DatePicker(
                        state = endDatePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false
                    )
                }
            }

            FilledTonalButton(onClick = {
                saveAsDefaults()
                navigator.hide()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Save as family default")
            }
        }
    }
}
