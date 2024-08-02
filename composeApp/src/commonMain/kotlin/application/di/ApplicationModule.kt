package application.di

import application.domain.ApplicationScreenModel
import org.koin.dsl.module

val applicationModule = module(true) {
    single { ApplicationScreenModel(get()) }
}
