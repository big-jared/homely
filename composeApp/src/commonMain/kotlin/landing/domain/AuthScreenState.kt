package landing.domain

import LoginDto
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import common.GeneralFailure
import kotlinx.coroutines.flow.MutableSharedFlow
import landing.data.AuthRepository
import landing.util.isValidEmail

sealed class AuthScreenState(protected val authRepository: AuthRepository) {
    val loading: MutableState<Boolean> = mutableStateOf(false)

    suspend fun signInWithGoogle() {
        authRepository.signInWithGoogle()
    }

    abstract suspend fun proceed()
}

sealed class SignInError(message: String) : GeneralFailure(message) {
    data object UnsetFields : SignInError("Missing required fields")
    data object InvalidCredentials : SignInError("Invalid credentials")
    data object ServerError : SignInError("Looks like something went wrong on our side. Please try again later.")
    data object NetworkError : SignInError("Connectivity issues, please check your network connection and try again.")
}

sealed class SignUpError(message: String) : GeneralFailure(message) {
    data object NotValidEmail : SignUpError("The email you entered is not valid.")
    data object PasswordsNotMatching : SignUpError("Password entries do not match")
    data object PasswordRequirementsNotMet : SignUpError("Password entered does not meet minimum security guidelines.")
    data object UnsetFields : SignUpError("Missing required information")
    data object ServerError : SignUpError("Looks like something went wrong on our side. Please try again later.")
    data object NetworkError : SignUpError("Connectivity issues, please check your network connection and try again.")
}

class SignInState(
    authRepository: AuthRepository,
    val forgotPassword: MutableState<Boolean> = mutableStateOf(false),
    val signInError: MutableSharedFlow<SignInError?> = MutableSharedFlow(),
    val emailInput: MutableState<String> = mutableStateOf(""),
    val passwordInput: MutableState<String> = mutableStateOf(""),
    val passwordVisible: MutableState<Boolean> = mutableStateOf(false),
) : AuthScreenState(authRepository) {

    suspend fun forgotPassword(toEmail: String) {
        // do something here
    }

    override suspend fun proceed() {
        val email = emailInput.value
        val password = passwordInput.value

        if (email.isEmpty() || password.isEmpty()) {
            signInError.emit(SignInError.UnsetFields)
            return
        }

        if (!email.isValidEmail()) {
            signInError.emit(SignInError.InvalidCredentials)
            return
        }

        loading.value = true

        try {
            authRepository.login(
                loginRequest = LoginDto(
                    email = emailInput.value,
                    password = passwordInput.value
                )
            )
        } catch (e: Exception) {
            // handle different exceptions here.
            signInError.emit(SignInError.InvalidCredentials)
        }

        loading.value = false
    }
}

class SignUpState(
    authRepository: AuthRepository,
    val signUpError: MutableSharedFlow<SignUpError?> = MutableSharedFlow(),
    val emailInput: MutableState<String> = mutableStateOf(""),
    val passwordInput: MutableState<String> = mutableStateOf(""),
    val passwordVisible: MutableState<Boolean> = mutableStateOf(false),
    val passwordRepeatInput: MutableState<String> = mutableStateOf(""),
    val passwordRepeatVisible: MutableState<Boolean> = mutableStateOf(false),
    val nameInput: MutableState<String> = mutableStateOf(""),
) : AuthScreenState(authRepository) {

    override suspend fun proceed() {
        val email = emailInput.value
        val password = passwordInput.value
        val confirmPassword = passwordRepeatInput.value
        val name = nameInput.value

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank()) {
            signUpError.emit(SignUpError.UnsetFields)
            return
        }

        if (!email.isValidEmail()) {
            signUpError.emit(SignUpError.NotValidEmail)
            return
        }

        if (password != confirmPassword) {
            signUpError.emit(SignUpError.PasswordsNotMatching)
            return
        }

        loading.value = true

        try {
            authRepository.login(
                loginRequest = LoginDto(
                    email = emailInput.value,
                    password = passwordInput.value
                )
            )
        } catch (e: Exception) {
            // handle different exceptions here.
            signUpError.emit(SignUpError.ServerError)
        }

        loading.value = false
    }
}
