package service

import UserRecord
import UserRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.dsl.module

val applicationModule = module(true) {
    single { ApplicationScreenModel(get()) }
}

enum class ApplicationTarget {
    Prod, Demo
}

val target = mutableStateOf(ApplicationTarget.Demo)

sealed class ApplicationState {
    class SignedIn(userRecord: UserRecord) : ApplicationState()
    data object SignedOut: ApplicationState()
}

class ApplicationScreenModel(private val userRepository: UserRepository) : ScreenModel {
    var seedColor = mutableStateOf(Color(0xff123456))
    var theme = mutableStateOf(PaletteStyle.TonalSpot)
    
    val isSignedIn = userRepository.currentUser.map { it != null }

    val initialized by lazy {
        flow {
            emit(false)
            initializeModules()
            emit(true)
        }
    }

    private suspend fun initializeModules() {
        userRepository.initalize()
    }
}
