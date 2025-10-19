package ru.rut.mad.data.repository

import ru.rut.mad.data.mapper.CatDtoToDomainMapper
import ru.rut.mad.data.network.ApiService
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.repository.ListRepository

class ListRepositoryImpl(
    private val apiService: ApiService,
    private val mapper: CatDtoToDomainMapper // Инжектим маппер
) : ListRepository {

    override suspend fun getElements(): Result<List<ListElementEntity>> {
        return try {
            // 1. Получаем "сырые" DTO из сети
            val dtoList = apiService.getCatImages()
            // 2. Мапим список DTO в список Entity
            val entityList = dtoList.map { dto -> mapper.map(dto) }
            // 3. Возвращаем успешный результат с уже "чистыми" данными
            Result.success(entityList)
        } catch (e: Exception) {
            // 4. В случае ошибки возвращаем failure
            Result.failure(e)
        }
    }
}