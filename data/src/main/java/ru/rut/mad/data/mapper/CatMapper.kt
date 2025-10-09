package ru.rut.mad.data.mapper

import ru.rut.mad.data.network.response.CatImageDto
import ru.rut.mad.domain.entity.ListElementEntity

fun CatImageDto.toDomain(): ListElementEntity {
    return ListElementEntity(
        id = this.id,
        title = "Cat #$id", // Генерируем простое название
        image = this.url,
        like = false // По умолчанию 'like' выключен
    )
}