import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.random.Random

interface AuthDataSource {
    suspend fun getLoggedInUser(): UserRecord?
    suspend fun login(loginRequest: LoginDto): UserRecord?
    suspend fun register(credentials: RegisterDto): UserRecord?
    suspend fun logout()
}

class AuthDataSourceProd(): AuthDataSource {
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

class AuthDataSourceDemo(): AuthDataSource {
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