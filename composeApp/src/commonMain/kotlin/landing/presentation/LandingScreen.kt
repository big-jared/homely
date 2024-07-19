package landing.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import application.presentation.AuthenticatedScreen
import application.presentation.FiraTypography
import application.presentation.primaryGreen
import cafe.adriel.voyager.core.screen.Screen
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.harmonize
import common.APP_NAME
import common.appAnimDuration
import homely.composeapp.generated.resources.Res
import homely.composeapp.generated.resources.app_icon_round
import homely.composeapp.generated.resources.google_logo
import kotlinx.coroutines.launch
import landing.domain.AuthScreenModel
import landing.domain.SignInState
import landing.domain.SignUpState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject


class LandingScreen : AuthenticatedScreen {

    @Composable
    override fun ScreenContent() {
        Column(modifier = Modifier.fillMaxSize()) {
            val authScreenModel = koinInject<AuthScreenModel>()

            Box {
                Surface(color = MaterialTheme.colorScheme.primary) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                        Text("Welcome to")
                        Text(
                            APP_NAME,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                    }
                }
                this@Column.AnimatedVisibility(
                    authScreenModel.showingSignUp.value,
                    enter = fadeIn(animationSpec = tween(appAnimDuration)),
                    exit = fadeOut(animationSpec = tween(appAnimDuration))
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                            Text("Welcome to")
                            Text(
                                APP_NAME,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }


            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                SignInContent(authScreenModel.signInState) { authScreenModel.showSignUp(true) }

                Column {
                    AnimatedVisibility(
                        authScreenModel.showingSignUp.value,
                        enter = slideInVertically(animationSpec = tween(appAnimDuration),
                            initialOffsetY = { it / 2 }),
                        exit = slideOutVertically(animationSpec = tween(appAnimDuration),
                            targetOffsetY = { it })
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary.darken(1.5f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Box {
                                SignUpContent(authScreenModel.signUpState) {
                                    authScreenModel.showSignUp(
                                        false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

