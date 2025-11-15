package ru.rut.mad.cleanapp.details.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rut.mad.cleanapp.details.DetailsScreenRoute
import ru.rut.mad.cleanapp.workers.FilterWorker
import ru.rut.mad.cleanapp.workers.KEY_FILTERED_URI
import ru.rut.mad.cleanapp.workers.KEY_IMAGE_URI
import ru.rut.mad.domain.usecase.GetCatsByIdUseCase
import java.util.UUID

class DetailsViewModel(
    private val getCatsByIdUseCase: GetCatsByIdUseCase,
    private val workManager: WorkManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

//    val state: StateFlow<DetailsState> = flow<DetailsState> {
//        val route: DetailsScreenRoute = savedStateHandle.toRoute()
//        val catId = route.id
//        getCatsByIdUseCase.execute(catId) // <-- ИСПРАВЛЕННЫЙ ВЫЗОВ
//            .collect { element ->
//                if (element != null) {
//                    emit(DetailsState.Content(element))
//                } else {
//                    emit(DetailsState.Error("Element with ID $catId not found in cache"))
//                }
//            }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = DetailsState.Loading
//    )

    // Меняем на MutableStateFlow, чтобы иметь возможность обновлять его изнутри
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val elementId = savedStateHandle.get<String>("id")
            if (elementId != null) {
                getCatsByIdUseCase.execute(elementId).collect { element ->
                    if (element != null) {
                        _state.value = DetailsState.Content(element)
                    } else {
                        _state.value = DetailsState.Error("Элемент с ID $elementId не найден")
                    }
                }
            }
        }
    }

    fun applyFilter() {
        val currentState = _state.value as? DetailsState.Content ?: return
        val currentImageUri = currentState.element.image

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val filterRequest = OneTimeWorkRequestBuilder<FilterWorker>()
            .setInputData(workDataOf(KEY_IMAGE_URI to currentImageUri))
            .setConstraints(constraints)
            .addTag("image_filter")
            .build()

        // Ставим задачу в очередь
        workManager.enqueue(filterRequest)

        // V-- ДОБАВЛЕНА ЛОГИКА ОТСЛЕЖИВАНИЯ РЕЗУЛЬТАТА --V
        observeWorkResult(filterRequest.id)
        // ^-- ДОБАВЛЕНА ЛОГИКА ОТСЛЕЖИВАНИЯ РЕЗУЛЬТАТА --^
    }

    private fun observeWorkResult(workId: UUID) {
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workId)
                // Отфильтровываем null значения, которые могут прийти в самом начале
                .filterNotNull()
                .collect { workInfo ->
                    // Проверяем, что работа завершилась успешно
                    // Используем полное имя WorkInfo.State.SUCCEEDED
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val filteredUri = workInfo.outputData.getString(KEY_FILTERED_URI)

                        if (filteredUri != null) {
                            val oldElement = (_state.value as? DetailsState.Content)?.element
                            if (oldElement != null) {
                                val newElement = oldElement.copy(image = filteredUri)
                                _state.value = DetailsState.Content(newElement)
                            }
                        }
                    }
                }
        }
    }
}