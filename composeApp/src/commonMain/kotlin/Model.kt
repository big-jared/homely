import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
class LoginDto(val email: String, val password: String)

@Serializable
data class UserRecord(
    val id: Int? = null,
    val name: String,
    val email: String,
    val authToken: String,
)