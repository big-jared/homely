package subscription.data

import SubscriptionType

interface SubscriptionDataSource {
    suspend fun getCurrentSubscription(): SubscriptionType?
    suspend fun selectSubscription(subscription: SubscriptionType)
    suspend fun cancelSubscription()
}

class SubscriptionDataSourceProd() : SubscriptionDataSource {
    override suspend fun getCurrentSubscription(): SubscriptionType {
        TODO("Not yet implemented")
    }

    override suspend fun selectSubscription(subscription: SubscriptionType) {
        TODO("Not yet implemented")
    }

    override suspend fun cancelSubscription() {
        TODO("Not yet implemented")
    }
}

class SubscriptionDataSourceDemo() : SubscriptionDataSource {
    private var currentSubscription: SubscriptionType? = null

    override suspend fun getCurrentSubscription(): SubscriptionType? = currentSubscription

    override suspend fun selectSubscription(subscription: SubscriptionType) {
        currentSubscription = subscription
    }

    override suspend fun cancelSubscription() {
        currentSubscription = null
    }
}
