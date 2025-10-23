package ru.rut.mad.data.di

import androidx.room.Room
import coil.ImageLoader
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.rut.mad.data.database.AppDatabase
import ru.rut.mad.data.database.CatCacheEntity
import ru.rut.mad.data.di.DiQualifiers.CACHE_TO_DOMAIN_MAPPER
import ru.rut.mad.data.di.DiQualifiers.DTO_TO_CACHE_MAPPER
import ru.rut.mad.data.mapper.CatCacheToDomainMapper
import ru.rut.mad.data.mapper.CatDtoToCacheMapper
import ru.rut.mad.data.network.ApiService
import ru.rut.mad.data.network.response.CatImageDto
import ru.rut.mad.data.repository.ListRepositoryImpl
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.mapper.Mapper
import ru.rut.mad.domain.repository.ListRepository

val dataModule = module {
    // Network (остается здесь, так как ApiService - деталь реализации)
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

    // Database
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database").build() }
    single { get<AppDatabase>().catDao() }

    // Mappers
    factory<Mapper<CatImageDto, CatCacheEntity>>(DTO_TO_CACHE_MAPPER) { CatDtoToCacheMapper() }
    factory<Mapper<CatCacheEntity, ListElementEntity>>(CACHE_TO_DOMAIN_MAPPER) { CatCacheToDomainMapper() }

    // Repository
    single<ListRepository> { ListRepositoryImpl(get(), get(), dtoToCacheMapper = get(qualifier = DTO_TO_CACHE_MAPPER),  cacheToDomainMapper = get(qualifier = CACHE_TO_DOMAIN_MAPPER), get(), androidContext()) } // 4 зависимости


    // ДОБАВЛЕНО: Провайдер для ImageLoader
    single {
        ImageLoader.Builder(androidContext())
            .respectCacheHeaders(false) // Важно для некоторых API
            .build()
    }

}