package common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BasicAlertDialog(title: String, message: String, buttonText: String = "Ok", onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onClose()
        },
        confirmButton = {
            Button(modifier = Modifier.padding(top = 24.dp), onClick = {
                onClose()
            }) {
                Text(buttonText,)
            }
        },
        modifier = Modifier,
        title = {
            Text(title)
        },
        text = {
            Column {
                Text(message)
            }
        },
    )
}