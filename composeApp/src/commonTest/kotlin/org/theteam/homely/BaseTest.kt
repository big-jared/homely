package org.theteam.homely

import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.BeforeTest

open class BaseTest(): KoinTest {

    @BeforeTest
    fun setup() {
        stopKoin()
    }
}