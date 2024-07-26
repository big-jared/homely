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
data class ParentUserRecord(
    val id: Int,
    val name: String,
    val email: String,
    val authToken: String,
)

@Serializable
data class Location(
    val state: String,
    val city: String,
    val lat: Double? = null,
    val long: Double? = null,
)

// What do you want to use the app for
// Simple (free): grade tracking, record keeping, pdf export
// Premium: State legal requirements, community Hub, Access to resources
// Elite: Automated grading, Automated course content, AI tutoring
enum class SubscriptionType() {
    Simple, Premium, Elite
}