package org.theteam.homely

import AuthDataSource
import AuthDataSourceDemo
import app.cash.turbine.test
import application.domain.ApplicationScreenModel
import kotlinx.coroutines.test.runTest
import landing.data.AuthRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test

class ApplicationTests: BaseTest() {

    @Test
    fun applicationInitializesSuccessfullyWhenLoggedIn() = runTest {
        startKoin {
            modules(
                module {
                    factory<AuthDataSource> { AuthDataSourceDemo(isLoggedIn = true) }
                    single { AuthRepository(get()) }
                    single { ApplicationScreenModel(get()) }
                })
        }
        
        val userRepository = get<AuthRepository>()
        val applicationScreenModel = get<ApplicationScreenModel>()
        applicationScreenModel.initialized.test {
            awaitItem() shouldEqual false
            awaitItem() shouldEqual true
            awaitComplete()
        }
        
        applicationScreenModel.isSignedIn.test {
            awaitItem() shouldEqual true
        }
        
        userRepository.currentUser.test {
            awaitItem()!!.email shouldEqual "lirundahusky@gmail.com"
        }
    }

    @Test
    fun applicationInitializesSuccessfullyNotLoggedIn() = runTest {
        startKoin {
            modules(
                module {
                    factory<AuthDataSource> { AuthDataSourceDemo(isLoggedIn = false) }
                    single { AuthRepository(get()) }
                    single { ApplicationScreenModel(get()) }
                })
        }

        val applicationScreenModel = get<ApplicationScreenModel>()
        applicationScreenModel.initialized.test {
            awaitItem() shouldEqual false
            awaitItem() shouldEqual true
            awaitComplete()
        }

        applicationScreenModel.isSignedIn.test {
            awaitItem() shouldEqual false
        }
    }
}