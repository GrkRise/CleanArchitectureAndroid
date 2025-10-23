package ru.rut.mad.data.mapper

import ru.rut.mad.data.database.CatCacheEntity
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.mapper.Mapper

class CatCacheToDomainMapper : Mapper<CatCacheEntity, ListElementEntity> {
    override fun map(input: CatCacheEntity): ListElementEntity {
        return ListElementEntity(
            id = input.id,
            title = "Cat #${input.id}",
            image = input.url,
            like = input.isLiked
        )
    }
}