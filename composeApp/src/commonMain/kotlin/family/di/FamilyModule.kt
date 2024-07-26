package family.di

import family.data.DemoFamilyDataSource
import family.data.FamilyDataSource
import family.data.FamilyRepository
import family.domain.FamilyInfoViewModel
import org.koin.dsl.module

val familyModule = module {
    single<FamilyDataSource> { DemoFamilyDataSource() }
    single { FamilyRepository(get()) }
    single { FamilyInfoViewModel(get()) }
}