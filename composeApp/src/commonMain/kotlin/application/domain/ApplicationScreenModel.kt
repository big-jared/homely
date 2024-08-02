package application.domain

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import landing.data.AuthRepository

enum class ApplicationTarget {
    Prod, Demo
}

val target = mutableStateOf(ApplicationTarget.Demo)

class ApplicationScreenModel(private val authRepository: AuthRepository) : ScreenModel {
    val isSignedIn = authRepository.currentUser.map { it != null }.distinctUntilChanged()

    val initialized by lazy {
        flow {
            emit(false)
            initializeModules()
            emit(true)
        }
    }

    private suspend fun initializeModules() {
        authRepository.initialize()
    }
}
