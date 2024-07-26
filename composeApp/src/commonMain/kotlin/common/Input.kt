package common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.harmonize

class Input {
}

@Composable
fun ConfigurableInput(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    onTextChange: (String) -> Unit,
    trailing: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(modifier = Modifier.padding().fillMaxWidth(),
        value = text,
        label = { Text(label) },
        onValueChange = { onTextChange(it) },
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(
                alpha = .5f
            )
        ),
        trailingIcon = {
            trailing?.invoke()
        })
}