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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import application.presentation.FiraTypography
import application.presentation.primaryGreen
import cafe.adriel.voyager.core.screen.Screen
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.harmonize
import homely.composeapp.generated.resources.Res
import homely.composeapp.generated.resources.app_icon_round
import homely.composeapp.generated.resources.google_logo
import kotlinx.coroutines.launch
import landing.domain.AuthScreenModel
import landing.domain.SignInState
import landing.domain.SignUpState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun ColumnScope.SignInWithGoogleButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.harmonize(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .padding(8.dp)
            .align(Alignment.CenterHorizontally)

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

class LandingScreen : Screen {
    @Composable
    fun BoxScope.SignInContent(state: SignInState, onSignUp: () -> Unit) {
        val coScope = rememberCoroutineScope()

        Column(modifier = Modifier.padding(top = 20.dp).align(Alignment.TopCenter)) {
            Spacer(modifier = Modifier.height(16.dp))
            SignInWithGoogleButton {
                coScope.launch { state.signInWithGoogle() }
            }
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp),
                text = "OR"
            )

            val focusManager = LocalFocusManager.current

            OutlinedTextField(
                modifier = Modifier.padding(top = 8.dp),
                value = state.emailInput.value,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                onValueChange = { state.emailInput.value = it },
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.padding(top = 8.dp),
                value = state.passwordInput.value,
                onValueChange = { state.passwordInput.value = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        coScope.launch {
                            focusManager.clearFocus()
                            state.proceed()
                        }
                    }
                ),
                label = { Text("Password") },
                singleLine = true
            )

            TextButton(
                onClick = {
                    coScope.launch { state.forgotPassword() }
                }, modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Forgot Password?")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                coScope.launch { state.proceed() }
            }) {
                Text("Sign In")
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { onSignUp() }) {
                Text("Sign Up")
            }
        }
    }

    @Composable
    fun BoxScope.SignUpContent(state: SignUpState, onSignIn: () -> Unit) {
        val coScope = rememberCoroutineScope()

        val focusManager = LocalFocusManager.current

        Column(modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)) {
            DynamicMaterialTheme(
                seedColor = primaryGreen.darken(),
                useDarkTheme = true,
                style = PaletteStyle.Rainbow,
                typography = FiraTypography()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SignInWithGoogleButton {
                    coScope.launch { state.signInWithGoogle() }
                }

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp), text = "OR"
                )

                OutlinedTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    value = state.emailInput.value,
                    onValueChange = { state.emailInput.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    label = { Text("Email") },
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    value = state.passwordInput.value,
                    onValueChange = { state.passwordInput.value = it },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    label = { Text("Password") },
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    value = state.passwordRepeatInput.value,
                    onValueChange = { state.passwordRepeatInput.value = it },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    label = { Text("Confirm Password") },
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    value = state.nameInput.value,
                    onValueChange = { state.nameInput.value = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            coScope.launch {
                                focusManager.clearFocus()
                                state.proceed()
                            }
                        }
                    ),
                    label = { Text("Full Name") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                    coScope.launch { state.proceed() }
                }) {
                    Text("Sign Up")
                }
                TextButton(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { onSignIn() }) {
                    Text("Sign In")
                }
            }
        }
    }

    @Composable
    override fun Content() {
        Column(modifier = Modifier.fillMaxSize()) {
            val authScreenModel = koinInject<AuthScreenModel>()
            val landingAnimDuration = 400

            Box {
                Surface(color = MaterialTheme.colorScheme.primary) {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                        Text("Welcome to")
                        Text(
                            "Grade Pal",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                    }
                }
                this@Column.AnimatedVisibility(
                    authScreenModel.showingSignUp.value,
                    enter = fadeIn(animationSpec = tween(landingAnimDuration)),
                    exit = fadeOut(animationSpec = tween(landingAnimDuration))
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                            Text("Welcome to")
                            Text(
                                "Grade Pal",
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
                            animationSpec = tween(landingAnimDuration),
                            initialOffsetY = { it / 2 }),
                        exit = slideOutVertically(
                            animationSpec = tween(landingAnimDuration),
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

