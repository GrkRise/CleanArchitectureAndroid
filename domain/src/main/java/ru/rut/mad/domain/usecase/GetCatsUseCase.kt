package ru.rut.mad.domain.usecase

import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.repository.ListRepository


class GetCatsUseCase(
    private val listRepository: ListRepository
) : UseCase<Unit, Result<List<ListElementEntity>>> {

    override suspend fun execute(data: Unit): Result<List<ListElementEntity>> {
        // UseCase просто делегирует вызов репозиторию.
        // Здесь может быть дополнительная бизнес-логика (сортировка, фильтрация),
        // но НЕ маппинг.
        return listRepository.getElements()
    }
}