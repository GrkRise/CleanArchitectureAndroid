package ru.rut.mad.cleanapp.di

import coil.ImageLoader
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.rut.mad.cleanapp.details.vm.DetailsViewModel
import ru.rut.mad.cleanapp.main.vm.MainViewModel
import java.util.concurrent.TimeUnit

val appModule = module {
    // Ключевое слово 'viewModel' сообщает Koin,
    // что это ViewModel и его жизненный цикл должен управляться соответственно.
    viewModel { MainViewModel() }
    viewModel { params -> // Используем params для передачи SavedStateHandle
        DetailsViewModel(
            savedStateHandle = params.get()
        )
    }

}