package landing.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import common.GeneralFailureDialog
import kotlinx.coroutines.launch
import landing.domain.SignInState

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
            value = state.emailInput.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            onValueChange = { state.emailInput.value = it },
            label = { Text("Email") },
            singleLine = true
        )

        OutlinedTextField(modifier = Modifier.padding(top = 8.dp),
            value = state.passwordInput.value,
            onValueChange = { state.passwordInput.value = it },
            visualTransformation = if (state.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                coScope.launch {
                    focusManager.clearFocus()
                    state.proceed()
                }
            }),
            label = { Text("Password") },
            singleLine = true,
            trailingIcon = {
                val passwordVisible = state.passwordVisible.value
                val image =
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { state.passwordVisible.value = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            })

        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                state.forgotPassword.value = true
            }
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

    Dialogs(state)
}

@Composable
private fun BoxScope.Dialogs(state: SignInState) {
    val coScope = rememberCoroutineScope()
    if (state.forgotPassword.value) {
        var forgotPasswordEmail by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        AlertDialog(
            onDismissRequest = {
                state.forgotPassword.value = false
            },
            confirmButton = {
                AnimatedVisibility(forgotPasswordEmail.isNotEmpty()) {
                    Button(modifier = Modifier.align(Alignment.Center)
                        .padding(top = 24.dp), onClick = {
                        state.forgotPassword.value = false
                    }) {
                        Text(
                            "Send reset link",
                        )
                    }
                }
            },
            modifier = Modifier,
            title = {
                Text("Forgot Password")
            },
            text = {
                Column {
                    Text("Enter the email address associated with your account.")
                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        onValueChange = { forgotPasswordEmail = it },
                        label = { Text("Email") },
                        singleLine = true
                    )
                }
            },
        )
    }

    val signInError = state.signInError.collectAsState(null).value
    signInError?.let { error ->
        GeneralFailureDialog(failure = error) {
            coScope.launch {
                state.signInError.emit(null)
            }
        }
    }
}