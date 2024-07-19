package landing.di

import AuthDataSource
import AuthDataSourceDemo
import AuthDataSourceProd
import application.domain.ApplicationTarget
import application.domain.target
import landing.data.AuthRepository
import org.koin.dsl.module
import landing.domain.AuthScreenModel

val authModule = module(true) {
    factory<AuthDataSource> {
        if (target.value == ApplicationTarget.Prod) {
            AuthDataSourceProd()
        } else {
            AuthDataSourceDemo()
        }
    }
    factory { AuthScreenModel(get()) }
    single { AuthRepository(get()) }
}