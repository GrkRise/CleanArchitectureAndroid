package ru.rut.mad.domain.repository

import ru.rut.mad.domain.entity.ListElementEntity
import kotlinx.coroutines.flow.Flow

interface ListRepository {
    // БЫЛО: suspend fun getElements(): Result<List<ListElementEntity>>
    // СТАЛО:
    fun getElements(): Flow<List<ListElementEntity>>

    // ДОБАВЛЕНО: Метод для экрана деталей
    fun getElement(id: String): Flow<ListElementEntity?>
}