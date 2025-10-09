package ru.rut.mad.cleanapp.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.rut.mad.domain.repository.ListRepository

class MainViewModel(
    private val listRepository: ListRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow()
    // Создаем канал для отправки "одноразовых" событий
    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()



    init {
//        viewModelScope.launch {
//            // ... логика симуляции загрузки ...
//            _state.emit(MainState.Content(sampleData)) // Убедитесь, что MainState.Content принимает List<ListElementEntity>
//        }

        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            listRepository.getElements()
                .onSuccess { list ->
                    _state.value = MainState.Content(list)
                }
                .onFailure { error ->
                    _state.value = MainState.Error(error.localizedMessage ?: "Unknown error")
                }
        }
    }


    // Метод, который будет вызывать UI
    fun onElementClick(elementId: String) {
        viewModelScope.launch {
            // Отправляем событие навигации в канал
            _navigationEvent.send(elementId)
        }
    }
}