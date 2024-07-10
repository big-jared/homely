import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import service.ApplicationTarget
import service.target
import util.isValidEmail
import kotlin.random.Random

val userModule = module(true) {
    factory<UserService> {
        if (target.value == ApplicationTarget.Prod) {
            UserServiceDemo()
        } else {
            UserServiceProd()
        }
    }
    factory { PreAuthScreenModel(get()) }
    single { UserRepository(get()) }
}

class PreAuthScreenModel(private val userRepository: UserRepository): ScreenModel {
    val emailInput = mutableStateOf("")
    val passwordInput = mutableStateOf("")
    val passwordRepeatInput = mutableStateOf("")
    val nameInput = mutableStateOf("")
    
    suspend fun signInWithGoogle() {
        userRepository.signInWithGoogle()
    }
    
    suspend fun login() {
        if (!emailInput.value.isValidEmail()) {
            throw Exception("Not valid email.")
        }
        
        userRepository.login(LoginDto(emailInput.value, passwordInput.value))
    }
    
    suspend fun signUp() {
        if (passwordInput != passwordRepeatInput) {
            throw Exception("Passwords not matching")
        }
        
        userRepository.register(
            RegisterDto(
                nameInput.value,
                emailInput.value,
                passwordInput.value,
            )
        )
    }
}

class UserRepository(private val userService: UserService): KoinComponent {
    val currentUser : SharedFlow<UserRecord?> get() = _currentUser
    private val _currentUser  = MutableStateFlow<UserRecord?>(null)
    
    suspend fun initalize() {
        processUser(userService.getLoggedInUser())
    }
    
    private suspend fun processUser(userRecord: UserRecord?) {
        _currentUser.emit(userRecord)
        // save auth token, do db stuff maybe
    }
    
    suspend fun login(loginRequest: LoginDto) = userService.login(loginRequest).also { processUser(it) }
    suspend fun register(registerRequest: RegisterDto) = userService.register(registerRequest).also { processUser(it) }
    suspend fun logout() = userService.logout().also { processUser(null) }
    
    // TODO
    suspend fun signInWithGoogle() {
        login(loginRequest = LoginDto("Temp", "Temp"))
    }
}

interface UserService {
    suspend fun getLoggedInUser(): UserRecord?
    suspend fun login(loginRequest: LoginDto): UserRecord?
    suspend fun register(credentials: RegisterDto): UserRecord?
    suspend fun logout()
}

class UserServiceProd(): UserService {
    override suspend fun getLoggedInUser(): UserRecord? {
        TODO("Not yet implemented")
    }
    
    override suspend fun login(loginRequest: LoginDto): UserRecord? = client.post("http://localhost:8081/user/login") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body<UserRecord?>()

    override suspend fun register(credentials: RegisterDto): UserRecord? = client.post("http://localhost:8081/user/signup") {
        contentType(ContentType.Application.Json)
        setBody(credentials)
    }.body<UserRecord?>()

    override suspend fun logout() {
        // Something
    }
}

class UserServiceDemo(): UserService {
    private val loggedInUser = UserRecord(
        id = Random.nextInt(),
        name = "Iamtest",
        username = "Bobert",
        authToken = Random.nextLong().toString()
    )
    
    override suspend fun getLoggedInUser(): UserRecord = loggedInUser
    
    override suspend fun login(loginRequest: LoginDto): UserRecord {
        return UserRecord(
            id = Random.nextInt(),
            name = "Iamtest",
            username = loginRequest.username,
            authToken = Random.nextLong().toString()
        )
    }

    override suspend fun register(credentials: RegisterDto): UserRecord {
        return UserRecord(
            id = Random.nextInt(),
            name = credentials.name,
            username = credentials.username,
            authToken = Random.nextLong().toString()
        )
    }

    override suspend fun logout() {
        // Something
    }
}