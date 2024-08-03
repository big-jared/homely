package org.theteam.homely

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import AuthDataSource
import AuthDataSourceDemo
import app.cash.turbine.test
import landing.data.AuthRepository
import landing.domain.AuthScreenModel
import landing.domain.SignInError
import landing.domain.SignUpError
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.get

class AuthTests : BaseTest() {

    private fun setupKoin() {
        startKoin {
            modules(
                module {
                    factory<AuthDataSource> { AuthDataSourceDemo() }
                    single { AuthRepository(get()) }
                    single { AuthScreenModel(get()) }
                })
        }
    }

    @Test
    fun loginWillLoadUser() = runTest {
        setupKoin()
        val userRepository = get<AuthRepository>()
        val model = get<AuthScreenModel>()

        model.signInState.emailInput.value = "liruthehusky@gmail.com"
        model.signInState.passwordInput.value = "Password1!"
        model.signInState.proceed()
        userRepository.currentUser.test {
            val user = awaitItem()!!
            user.email shouldEqual "liruthehusky@gmail.com"
        }
    }

    @Test
    fun loginSetsEmailFormatErrorCorrectly() = runTest {
        setupKoin()
        val model = get<AuthScreenModel>()
        model.signInState.signInError.test {
            model.signInState.emailInput.value = "liruthehusky"
            model.signInState.passwordInput.value = "Password1!"
            model.signInState.proceed()
            val error = awaitItem()!!

            error::class shouldEqual SignInError.InvalidCredentials::class
        }
    }

    @Test
    fun loginSetsMissingFieldsError() = runTest {
        setupKoin()
        val model = get<AuthScreenModel>()
        model.signInState.signInError.test {
            model.signInState.emailInput.value = ""
            model.signInState.passwordInput.value = "Password1!"
            model.signInState.proceed()
            val error = awaitItem()!!

            error::class shouldEqual SignInError.UnsetFields::class
        }
    }

    @Test
    fun successfulRegisterWorks() = runTest {
        setupKoin()
        val userRepository = get<AuthRepository>()
        val model = get<AuthScreenModel>()

        model.signUpState.emailInput.value = "liruthehusky@gmail.com"
        model.signUpState.passwordInput.value = "Password1!"
        model.signUpState.passwordRepeatInput.value = "Password1!"
        model.signUpState.nameInput.value = "LIRUNDA"
        model.signUpState.proceed()

        userRepository.currentUser.test {
            val user = awaitItem()!!
            user.email shouldEqual "liruthehusky@gmail.com"
        }
    }

    @Test
    fun registerSetsEmailFormatErrorCorrectly() = runTest {
        setupKoin()
        val model = get<AuthScreenModel>()
        model.signUpState.signUpError.test {
            model.signUpState.emailInput.value = "liruthehusky"
            model.signUpState.passwordInput.value = "Password1!"
            model.signUpState.passwordRepeatInput.value = "Password1!"
            model.signUpState.nameInput.value = "LIRUNDA"
            model.signUpState.proceed()
            val error = awaitItem()!!

            error::class shouldEqual SignUpError.NotValidEmail::class
        }
    }

    @Test
    fun registerSetsPassworsNotMatchingErrorCorrectly() = runTest {
        setupKoin()
        val model = get<AuthScreenModel>()
        model.signUpState.signUpError.test {
            model.signUpState.emailInput.value = "liruthehusky@gmail.com"
            model.signUpState.passwordInput.value = "Password1!"
            model.signUpState.passwordRepeatInput.value = "Password1"
            model.signUpState.nameInput.value = "LIRUNDA"
            model.signUpState.proceed()
            val error = awaitItem()!!

            error::class shouldEqual SignUpError.PasswordsNotMatching::class
        }
    }

    @Test
    fun registerSetsMissingFieldsErrorCorrectly() = runTest {
        setupKoin()
        val model = get<AuthScreenModel>()
        model.signUpState.signUpError.test {
            model.signUpState.emailInput.value = "liruthehusky@gmail.com"
            model.signUpState.passwordInput.value = "Password1!"
            model.signUpState.passwordRepeatInput.value = "Password1"
            model.signUpState.proceed()
            val error = awaitItem()!!

            error::class shouldEqual SignUpError.UnsetFields::class
        }
    }
}