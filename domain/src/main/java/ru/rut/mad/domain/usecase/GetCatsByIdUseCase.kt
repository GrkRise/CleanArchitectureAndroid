package ru.rut.mad.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.repository.ListRepository


class GetCatsByIdUseCase(
    private val listRepository: ListRepository
) : UseCase<String, Flow<ListElementEntity?>> {

    override fun execute(data: String): Flow<ListElementEntity?> {
        // UseCase просто делегирует вызов репозиторию.
        // Здесь может быть дополнительная бизнес-логика (сортировка, фильтрация),
        // но НЕ маппинг.
        return listRepository.getElement(data)
    }
}