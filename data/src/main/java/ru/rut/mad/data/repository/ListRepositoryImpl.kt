package ru.rut.mad.data.repository

import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.rut.mad.data.database.CatCacheEntity
import ru.rut.mad.data.database.CatDao
import ru.rut.mad.data.network.ApiService
import ru.rut.mad.data.network.response.CatImageDto
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.mapper.Mapper
import ru.rut.mad.domain.repository.ListRepository

class ListRepositoryImpl(
    private val apiService: ApiService,
    private val catDao: CatDao,
    private val dtoToCacheMapper: Mapper<CatImageDto, CatCacheEntity>,
    private val cacheToDomainMapper: Mapper<CatCacheEntity, ListElementEntity>,// Инжектим маппер
    private val imageLoader: ImageLoader, // <-- НОВАЯ ЗАВИСИМОСТЬ
    private val applicationContext: android.content.Context // <-- НУЖЕН КОНТЕКСТ
) : ListRepository {

    override fun getElements(): Flow<List<ListElementEntity>> {
        return catDao.getCatsFlow() // 1. Берем постоянный поток данных из БД
            .map { catsFromCache -> // 2. Маппим каждую новую порцию данных в доменную модель
                catsFromCache.map { cacheToDomainMapper.map(it) }
            }
            .onStart { // 3. При первой подписке на этот Flow, выполняем этот блок
                try {
                    // Пытаемся загрузить свежие данные из сети
                    val catsFromApi = apiService.getCatImages()
                    val cacheEntities = catsFromApi.map { dtoToCacheMapper.map(it) }
                    // И сохраняем их в БД. Room автоматически разошлет
                    // это обновление всем, кто подписан на getCatsFlow()
                    catDao.clearAndInsert(cacheEntities)

                    // НОВАЯ ЛОГИКА: Запускаем pre-fetching картинок
                    precacheImages(cacheEntities)

                } catch (e: Exception) {
                    Log.e("ListRepositoryImpl", "Network update failed", e)
                    // Если сеть упала, ничего страшного. Flow продолжит работать,
                    // отдавая старые данные из кэша.
                }
            }
    }

    // НОВЫЙ МЕТОД
    private fun precacheImages(cats: List<CatCacheEntity>) {
        cats.forEach { cat ->
            val request = ImageRequest.Builder(applicationContext)
                .data(cat.url)
                // Опционально: можно указать, куда кэшировать (только диск)
                // .diskCachePolicy(CachePolicy.ENABLED)
                .build()
            imageLoader.enqueue(request)
        }
    }

    override fun getElement(id: String): Flow<ListElementEntity?> {
        // Для одного элемента обновление из сети не делаем (для простоты),
        // просто берем актуальные данные из кэша.
        return catDao.getCatById(id).map { catFromCache ->
            catFromCache?.let { cacheToDomainMapper.map(it) }
        }
    }
}