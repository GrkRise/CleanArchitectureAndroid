package ru.rut.mad.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class CatImageDto(
    val id: String,
    val url: String
)