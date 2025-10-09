package ru.rut.mad.cleanapp.details.vm

import ru.rut.mad.domain.entity.ListElementEntity

sealed class DetailsState {
    data object Loading : DetailsState()
    data class Error(val message: String) : DetailsState()
    data class Content(val element: ListElementEntity) : DetailsState()
}