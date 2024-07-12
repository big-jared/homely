package landing.data

import LoginDto
import RegisterDto
import AuthDataSource
import UserRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class AuthRepository(private val userDataSource: AuthDataSource) {
    val currentUser : SharedFlow<UserRecord?> get() = _currentUser
    private val _currentUser  = MutableStateFlow<UserRecord?>(null)

    suspend fun initialize() {
        processUser(userDataSource.getLoggedInUser())
    }

    private suspend fun processUser(userRecord: UserRecord?) {
        _currentUser.emit(userRecord)
        // save auth token, do db stuff maybe
    }

    suspend fun login(loginRequest: LoginDto) = userDataSource.login(loginRequest).also { processUser(it) }
    suspend fun register(registerRequest: RegisterDto) = userDataSource.register(registerRequest).also { processUser(it) }
    suspend fun logout() = userDataSource.logout().also { processUser(null) }

    // TODO
    suspend fun signInWithGoogle() {
        login(loginRequest = LoginDto("Temp", "Temp"))
    }
}