import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val name: String,
    val username: String,
    val password: String,
)

@Serializable
class LoginDto(val username: String, val password: String)

@Serializable
data class UserRecord(
    val id: Int? = null,
    val name: String,
    val username: String,
    val authToken: String,
)