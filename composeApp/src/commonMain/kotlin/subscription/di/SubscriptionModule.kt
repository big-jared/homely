package subscription.di

import application.domain.ApplicationTarget
import application.domain.target
import org.koin.dsl.module
import subscription.data.SubscriptionDataSource
import subscription.data.SubscriptionDataSourceDemo
import subscription.data.SubscriptionDataSourceProd
import subscription.data.SubscriptionRepository
import subscription.domain.SubscriptionViewModel

val subscriptionModule = module(true) {
    factory<SubscriptionDataSource> {
        if (target.value == ApplicationTarget.Prod) {
            SubscriptionDataSourceProd()
        } else {
            SubscriptionDataSourceDemo()
        }
    }
    single { SubscriptionRepository(get()) }
    factory { SubscriptionViewModel(get()) }
}