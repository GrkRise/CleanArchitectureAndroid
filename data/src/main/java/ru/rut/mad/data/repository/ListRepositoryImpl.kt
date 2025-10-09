package ru.rut.mad.data.repository

import ru.rut.mad.data.mapper.toDomain
import ru.rut.mad.data.network.ApiService
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.repository.ListRepository

class ListRepositoryImpl(
    private val apiService: ApiService
) : ListRepository {
    override suspend fun getElements(): Result<List<ListElementEntity>> {
        return try {
            val dtoList = apiService.getCatImages()
            Result.success(dtoList.map { it.toDomain() })
        } catch (e: Exception) {
            // В реальном проекте ошибки нужно обрабатывать более гранулярно
            Result.failure(e)
        }
    }
}