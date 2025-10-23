package ru.rut.mad.data.mapper

import ru.rut.mad.data.database.CatCacheEntity
import ru.rut.mad.data.network.response.CatImageDto
import ru.rut.mad.domain.mapper.Mapper

class CatDtoToCacheMapper : Mapper<CatImageDto, CatCacheEntity> {
    override fun map(input: CatImageDto): CatCacheEntity {
        return CatCacheEntity(id = input.id, url = input.url)
    }
}