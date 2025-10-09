package ru.rut.mad.cleanapp.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.rut.mad.cleanapp.details.vm.DetailsViewModel
import ru.rut.mad.cleanapp.main.vm.MainViewModel
import ru.rut.mad.data.network.ApiService
import retrofit2.Retrofit
import ru.rut.mad.data.repository.ListRepositoryImpl
import ru.rut.mad.domain.repository.ListRepository

val appModule = module {
    // Ключевое слово 'viewModel' сообщает Koin,
    // что это ViewModel и его жизненный цикл должен управляться соответственно.
    viewModel { MainViewModel(listRepository = get()) }
    // ДОБАВЛЕНО: Регистрация ViewModel для экрана деталей
    viewModel { params -> // Используем params для передачи SavedStateHandle
        DetailsViewModel(
            savedStateHandle = params.get()
        )
    }

    // Network
    single<ApiService> {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }

    // Repository
    single<ListRepository> { ListRepositoryImpl(apiService = get()) }

}