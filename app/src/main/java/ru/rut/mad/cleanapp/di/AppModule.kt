package ru.rut.mad.cleanapp.di

import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.rut.mad.cleanapp.details.vm.DetailsViewModel
import ru.rut.mad.cleanapp.main.vm.MainViewModel
import ru.rut.mad.cleanapp.workers.FilterWorker
import ru.rut.mad.domain.usecase.GetCatsUseCase
import ru.rut.mad.domain.usecase.GetCatsByIdUseCase

val appModule = module {
    // ViewModel зависит только от GetCatsUseCase
    viewModel { MainViewModel(get()) }
    // ДОБАВЛЕНО: Регистрация ViewModel для экрана деталей
    viewModel { params -> // Используем params для передачи SavedStateHandle
        DetailsViewModel(
            getCatsByIdUseCase = get<GetCatsByIdUseCase>(),
            workManager = get(), // <-- ЯВНОЕ УКАЗАНИЕ ТИПА
            savedStateHandle = params.get()
        )
    }

    // GetCatsUseCase зависит только от ListRepository
    factory { GetCatsUseCase(get()) }
    factory { GetCatsByIdUseCase(get()) }

    // Регистрируем WorkManager как singleton
    single { WorkManager.getInstance(androidContext()) }

    // Регистрируем наш Worker с помощью специального dsl 'worker'
    worker { FilterWorker(get(), get(), get()) }


}