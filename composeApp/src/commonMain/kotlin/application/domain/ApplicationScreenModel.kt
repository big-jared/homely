package application.domain

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import landing.data.AuthRepository

enum class ApplicationTarget {
    Prod, Demo
}

val target = mutableStateOf(ApplicationTarget.Demo)

class ApplicationScreenModel(private val authRepository: AuthRepository) : ScreenModel {
    var seedColor = mutableStateOf(Color(0xff1abc9c))
    var theme = mutableStateOf(PaletteStyle.Rainbow)

    val isSignedIn = authRepository.currentUser.map { it != null }

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