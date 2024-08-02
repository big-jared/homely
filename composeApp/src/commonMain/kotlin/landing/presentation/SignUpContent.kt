package landing.presentation

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import application.presentation.FiraTypography
import application.presentation.primaryGreen
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.darken
import common.GeneralFailureDialog
import kotlinx.coroutines.launch
import landing.domain.SignUpState

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
                    .padding(vertical = 16.dp),
                text = "OR"
            )

            OutlinedTextField(
                value = state.emailInput.value,
                onValueChange = { state.emailInput.value = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                label = { Text("Email") },
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.padding(top = 8.dp),
                value = state.passwordInput.value,
                onValueChange = { state.passwordInput.value = it },
                visualTransformation = if (state.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
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
                }
            )

            OutlinedTextField(
                modifier = Modifier.padding(top = 8.dp),
                value = state.passwordRepeatInput.value,
                onValueChange = { state.passwordRepeatInput.value = it },
                visualTransformation = if (state.passwordRepeatVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                label = { Text("Confirm Password") },
                singleLine = true,
                trailingIcon = {
                    val passwordVisible = state.passwordRepeatVisible.value
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { state.passwordRepeatVisible.value = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier.padding(top = 8.dp),
                value = state.nameInput.value,
                onValueChange = { state.nameInput.value = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    coScope.launch {
                        focusManager.clearFocus()
                        state.proceed()
                    }
                }),
                label = { Text("Full Name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                coScope.launch { state.proceed() }
            }) {
                Text("Sign Up")
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { onSignIn() }
            ) {
                Text("Sign In")
            }
        }
    }

    val signUpError = state.signUpError.collectAsState(null).value
    signUpError?.let { error ->
        GeneralFailureDialog(failure = error) {
            coScope.launch {
                state.signUpError.emit(null)
            }
        }
    }
}
