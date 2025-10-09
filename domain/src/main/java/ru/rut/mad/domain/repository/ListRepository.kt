package ru.rut.mad.domain.repository

import ru.rut.mad.domain.entity.ListElementEntity

interface ListRepository {
    suspend fun getElements(): Result<List<ListElementEntity>>
}