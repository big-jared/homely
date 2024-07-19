import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.random.Random

interface AuthDataSource {
    suspend fun getLoggedInUser(): ParentUserRecord?
    suspend fun login(loginRequest: LoginDto): ParentUserRecord?
    suspend fun register(credentials: RegisterDto): ParentUserRecord?
    suspend fun logout()
}

class AuthDataSourceProd(): AuthDataSource {
    override suspend fun getLoggedInUser(): ParentUserRecord? {
        TODO("Not yet implemented")
    }
    
    override suspend fun login(loginRequest: LoginDto): ParentUserRecord? = client.post("http://localhost:8081/user/login") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body<ParentUserRecord?>()

    override suspend fun register(credentials: RegisterDto): ParentUserRecord? = client.post("http://localhost:8081/user/signup") {
        contentType(ContentType.Application.Json)
        setBody(credentials)
    }.body<ParentUserRecord?>()

    override suspend fun logout() {
        // Something
    }
}

class AuthDataSourceDemo(isLoggedIn: Boolean = false): AuthDataSource {
    private var loggedInUser = if (isLoggedIn) ParentUserRecord(
        id = Random.nextInt(),
        name = "Iamtest",
        email = "lirundahusky@gmail.com",
        authToken = Random.nextLong().toString()
    ) else null
    
    override suspend fun getLoggedInUser(): ParentUserRecord? = loggedInUser
    
    override suspend fun login(loginRequest: LoginDto): ParentUserRecord {
        return ParentUserRecord(
            id = Random.nextInt(),
            name = "Iamtest",
            email = loginRequest.email,
            authToken = Random.nextLong().toString()
        )
    }

    override suspend fun register(credentials: RegisterDto): ParentUserRecord {
        return ParentUserRecord(
            id = Random.nextInt(),
            name = credentials.name,
            email = credentials.email,
            authToken = Random.nextLong().toString()
        )
    }

    override suspend fun logout() {
        // Something
    }
}