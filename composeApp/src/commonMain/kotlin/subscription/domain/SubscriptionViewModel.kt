package subscription.domain

import SubscriptionType
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import subscription.data.SubscriptionRepository
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

enum class SubscriptionOption(
    val title: String,
    val features: List<String>,
    val monthlyPrice: Double?,
    val trialLength: Duration? = null,
    val type: SubscriptionType
) {
    Simple(
        title = "Simple",
        features = listOf(
            "Grade Tracking",
            "Course Analysis",
            "Recorded Keeping",
            "PDF export"
        ),
        monthlyPrice = null,
        trialLength = Duration.INFINITE,
        type = SubscriptionType.Simple
    ),
    Premium(
        title = "Premium",
        features = listOf(
            "Meet your states Legal requirements",
            "Community Hub",
            "Access to our propitiatory curriculums and resources"
        ),
        monthlyPrice = 20.00,
        trialLength = 14.days,
        type = SubscriptionType.Premium
    ),
    Elite(
        title = "Elite",
        features = listOf(
            "Automated grading",
            "Automated assignments / tests",
            "Interactive AI tutoring"
        ),
        monthlyPrice = 40.00,
        trialLength = 7.days,
        type = SubscriptionType.Elite
    )
}

class SubscriptionViewModel(private val subscriptionRepository: SubscriptionRepository) : ScreenModel {
    val options = listOf(SubscriptionOption.Simple, SubscriptionOption.Premium, SubscriptionOption.Elite)
    val selectedSubscription = MutableStateFlow<SubscriptionType?>(null)

    suspend fun initialize() {
        subscriptionRepository.initSubscription()
    }

    suspend fun changeSubscription() {
        subscriptionRepository.subscribe(selectedSubscription.value ?: return)
    }
}
