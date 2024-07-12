package landing.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import landing.data.AuthRepository

data class DialogContents(
    val title: String,
    val message: String,
)

sealed class AuthScreenState(private val authRepository: AuthRepository) {
    val dialog: MutableState<DialogContents?> = mutableStateOf(null)
    val loading: MutableState<Boolean> = mutableStateOf(false)

    suspend fun signInWithGoogle() {

    }

    open suspend fun proceed() {

    }
}

class SignInState(
    authRepository: AuthRepository,
    val emailInput: MutableState<String> = mutableStateOf(""),
    val passwordInput: MutableState<String> = mutableStateOf(""),
) : AuthScreenState(authRepository) {

    suspend fun forgotPassword() {

    }

    override suspend fun proceed() {
        TODO("Not yet implemented")
    }
}

class SignUpState(
    authRepository: AuthRepository,
    val emailInput: MutableState<String> = mutableStateOf(""),
    val passwordInput: MutableState<String> = mutableStateOf(""),
    val passwordRepeatInput: MutableState<String> = mutableStateOf(""),
    val nameInput: MutableState<String> = mutableStateOf(""),
) : AuthScreenState(authRepository) {

    override suspend fun proceed() {
        TODO("Not yet implemented")
    }
}

class AuthScreenModel(authRepository: AuthRepository) : ScreenModel {
    val signInState = SignInState(authRepository)
    val signUpState = SignUpState(authRepository)

    val showingSignUp = mutableStateOf(false)

    fun showSignUp(showing: Boolean) {
        showingSignUp.value = showing
    }
}