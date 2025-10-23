package ru.rut.mad.cleanapp.details.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.rut.mad.cleanapp.details.DetailsScreenRoute
import ru.rut.mad.domain.usecase.GetCatsByIdUseCase

class DetailsViewModel(
    private val getCatsByIdUseCase: GetCatsByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<DetailsState> = flow<DetailsState> {
        val route: DetailsScreenRoute = savedStateHandle.toRoute()
        val catId = route.id
        getCatsByIdUseCase.execute(catId) // <-- ИСПРАВЛЕННЫЙ ВЫЗОВ
            .collect { element ->
                if (element != null) {
                    emit(DetailsState.Content(element))
                } else {
                    emit(DetailsState.Error("Element with ID $catId not found in cache"))
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsState.Loading
    )
}