package landing.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.harmonize
import homely.composeapp.generated.resources.Res
import homely.composeapp.generated.resources.google_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun ColumnScope.SignInWithGoogleButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.primaryContainer.harmonize(MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp)
        ).clip(RoundedCornerShape(8.dp)).clickable {
            onClick()
        }.padding(8.dp).align(Alignment.CenterHorizontally)

    ) {
        Image(
            modifier = Modifier.size(32.dp).align(Alignment.CenterVertically),
            painter = painterResource(Res.drawable.google_logo),
            contentDescription = ""
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
            text = "Sign in with Google"
        )
    }
}
