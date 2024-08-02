package common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.harmonize

@Composable
fun AppIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    painter: Painter = rememberVectorPainter(Icons.Rounded.Add),
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer.harmonize(MaterialTheme.colorScheme.primary),
    contentColor: Color = MaterialTheme.colorScheme.secondary.harmonize(MaterialTheme.colorScheme.primary),
    ratio: Float = .6f
) {
    FilledTonalIconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor)
    ) {
        Icon(modifier = Modifier.fillMaxSize(ratio), painter = painter, contentDescription = "", tint = contentColor)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HighlightBox(
    modifier: Modifier = Modifier,
    color: Color? = null,
    backgroundColor: Color? = null,
    frontIcon: (@Composable () -> Unit)? = null,
    backIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    text: String?,
    textModifier: Modifier = Modifier,
) {
    val mainColor = color ?: MaterialTheme.colorScheme.primary
    val background = backgroundColor ?: MaterialTheme.colorScheme.primaryContainer

    Row(
        modifier
            .background(background, shape = CircleShape)
            .clip(CircleShape)
            .combinedClickable(onClick = { onClick() }, onLongClick = { onLongClick() })
    ) {
        if (frontIcon != null) {
            Box(modifier = Modifier.padding(12.dp).size(16.dp).align(Alignment.CenterVertically)) {
                frontIcon()
            }
        }
        text?.let {
            Text(
                modifier = if (frontIcon == null) {
                    textModifier.padding(16.dp)
                } else {
                    textModifier
                        .padding(end = 12.dp)
                        .align(Alignment.CenterVertically)
                },
                text = text,
                color = mainColor,
                style = textStyle
            )
        }
        if (backIcon != null) {
            Box(modifier = Modifier.padding(12.dp).size(32.dp)) {
                backIcon()
            }
        }
    }
}
