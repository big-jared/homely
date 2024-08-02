package landing.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import application.presentation.AuthenticatedScreen
import com.materialkolor.ktx.darken
import common.APP_NAME
import common.appAnimDuration
import landing.domain.AuthScreenModel
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
                        enter = slideInVertically(
                            animationSpec = tween(appAnimDuration),
                            initialOffsetY = { it / 2 }
                        ),
                        exit = slideOutVertically(
                            animationSpec = tween(appAnimDuration),
                            targetOffsetY = { it }
                        )
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
