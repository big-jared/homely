package common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

abstract class GeneralFailure(val message: String)

@Composable
fun GeneralFailureDialog(
    modifier: Modifier = Modifier,
    failure: GeneralFailure,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Box {
                Button(
                    modifier = Modifier.align(Alignment.Center)
                        .padding(top = 24.dp),
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(
                        "Ok",
                    )
                }
            }
        },
        modifier = Modifier,
        title = {
            Text("An error occurred")
        },
        text = {
            Text(failure.message)
        },
    )
}
