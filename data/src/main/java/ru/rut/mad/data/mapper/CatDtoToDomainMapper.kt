package ru.rut.mad.data.mapper

import ru.rut.mad.data.network.response.CatImageDto
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.mapper.Mapper

class CatDtoToDomainMapper : Mapper<CatImageDto, ListElementEntity> {
    override fun map(input: CatImageDto): ListElementEntity {
        return ListElementEntity(
            id = input.id,
            // Правильно мапим поле 'url' из DTO в поле 'image' в Entity
            image = input.url,
            // API не предоставляет нам название, поэтому мы генерируем его сами.
            // Это тоже часть логики маппинга.
            title = "Cat #${input.id}",
            // По умолчанию, когда мы загружаем кота из сети, он не "лайкнут".
            like = false
        )
    }
}