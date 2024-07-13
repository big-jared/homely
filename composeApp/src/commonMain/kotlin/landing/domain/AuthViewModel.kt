package landing.domain

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import landing.data.AuthRepository


class AuthScreenModel(authRepository: AuthRepository) : ScreenModel {
    val signInState = SignInState(authRepository)
    val signUpState = SignUpState(authRepository)

    val showingSignUp = mutableStateOf(false)

    fun showSignUp(showing: Boolean) {
        showingSignUp.value = showing
    }
}