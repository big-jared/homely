package course.di

import course.data.CourseDataSource
import course.data.SchoolingRepository
import course.data.DemoCourseDataSource
import course.domain.CoursesViewModel
import org.koin.dsl.module

val coursesModule = module {
    single<CourseDataSource> { DemoCourseDataSource() }
    single { SchoolingRepository(get()) }
    single { CoursesViewModel(get(), get(), get()) }
}