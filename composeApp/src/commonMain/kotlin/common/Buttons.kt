package common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun AppIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    painter: Painter = rememberVectorPainter(Icons.Rounded.Add),
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.secondary,
    ratio: Float = .6f
) {
    FilledTonalIconButton(
        modifier = modifier, onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor)
    ) {
        Icon(modifier = Modifier.fillMaxSize(ratio), painter = painter, contentDescription = "", tint = contentColor)
    }
}