package org.sodergutt.homely

import UserRepository
import UserService
import UserServiceDemo
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import service.ApplicationScreenModel
import kotlin.test.Test

class ApplicationServiceTests: KoinTest {

    @Test
    fun applicationInitializesSuccessfully() = runTest {
        startKoin {
            modules(
                module {
                    factory<UserService> { UserServiceDemo() }
                    single { UserRepository(get()) }
                    single { ApplicationScreenModel(get()) }
                })
        }
        
        val userRepository = get<UserRepository>()
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
            awaitItem()!!.username shouldEqual "Bobert"
        }
    }
}