package landing.data

import AuthDataSource
import LoginDto
import ParentUserRecord
import RegisterDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AuthRepository(private val userDataSource: AuthDataSource) {
    val currentUser: SharedFlow<ParentUserRecord?> get() = _currentUser
    private val _currentUser = MutableSharedFlow<ParentUserRecord?>()

    suspend fun initialize() {
        processUser(userDataSource.getLoggedInUser())
    }

    private suspend fun processUser(userRecord: ParentUserRecord?) {
        _currentUser.emit(userRecord)
        // save auth token, do db stuff maybe
    }

    suspend fun login(loginRequest: LoginDto) = userDataSource.login(loginRequest).also { processUser(it) }
    suspend fun register(registerRequest: RegisterDto) = userDataSource.register(
        registerRequest
    ).also { processUser(it) }
    suspend fun logout() = userDataSource.logout().also { processUser(null) }

    // TODO
    suspend fun signInWithGoogle() {
        login(loginRequest = LoginDto("Temp", "Temp"))
    }
}
