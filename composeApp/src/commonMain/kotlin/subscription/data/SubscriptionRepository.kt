package subscription.data

import SubscriptionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SubscriptionRepository(private val subscriptionDataSource: SubscriptionDataSource) {
    val subscription: Flow<SubscriptionType?> get() = _subscription
    private val _subscription = MutableStateFlow<SubscriptionType?>(null)

    suspend fun initSubscription() {
        _subscription.emit(subscriptionDataSource.getCurrentSubscription())
    }

    suspend fun subscribe(type: SubscriptionType) {
        subscriptionDataSource.selectSubscription(type)
        _subscription.emit(type)
    }

    suspend fun cancel() {
        subscriptionDataSource.cancelSubscription()
        _subscription.emit(null)
    }
}
