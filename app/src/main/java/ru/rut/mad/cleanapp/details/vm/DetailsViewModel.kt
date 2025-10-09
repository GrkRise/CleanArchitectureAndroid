package ru.rut.mad.cleanapp.details.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.rut.mad.cleanapp.details.DetailsScreenRoute
import ru.rut.mad.domain.entity.ListElementEntity

class DetailsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state: StateFlow<DetailsState> get() = _state

    // Временно создадим здесь тот же самый список для симуляции
    private val sampleData = listOf(
        ListElementEntity("1", "Cool Cat", "https://cataas.com/cat/says/hello", true),
        ListElementEntity("2", "Serious Cat", "https://cataas.com/cat", false),
        ListElementEntity("3", "Cute Cat", "https://cataas.com/cat/cute", true)
    )

    init {
        viewModelScope.launch {
            val routeInfo = savedStateHandle.toRoute<DetailsScreenRoute>()
            val elementId = routeInfo.id

            val element = sampleData.find { it.id == elementId }

            if (element != null) {
                _state.emit(DetailsState.Content(element))
            } else {
                _state.emit(DetailsState.Error("Элемент с ID $elementId не найден"))
            }
        }
    }
}