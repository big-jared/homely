package state.di

import org.koin.dsl.module
import state.data.DemoStateDataSource
import state.data.StateDataSource
import state.data.StateRepository

val stateModule = module {
    single<StateDataSource> { DemoStateDataSource() }
    single { StateRepository(get()) }
}
