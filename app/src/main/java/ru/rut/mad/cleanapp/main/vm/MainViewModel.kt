package ru.rut.mad.cleanapp.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.rut.mad.domain.entity.ListElementEntity

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow()
    // Создаем канал для отправки "одноразовых" событий
    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()


    // Статичные данные из прошлого задания
    private val sampleData = listOf(
        ListElementEntity("1", "Cool Cat", "https://cataas.com/cat/says/hello", true),
        ListElementEntity("2", "Serious Cat", "https://cataas.com/cat", false),
        ListElementEntity("3", "Cute Cat", "https://cataas.com/cat/cute", true)
    )

    init {
        viewModelScope.launch {
            // ... логика симуляции загрузки ...
            _state.emit(MainState.Content(sampleData)) // Убедитесь, что MainState.Content принимает List<ListElementEntity>
        }

        //loadData()
    }

    private fun loadData() {
        // Используем viewModelScope для запуска корутины,
        // которая будет автоматически отменена, когда ViewModel уничтожится.
        viewModelScope.launch {
            _state.value = MainState.Loading
            // Симулируем задержку сети в 2 секунды
            delay(2000)
            // Передаем данные в состояние Content
            _state.value = MainState.Content(sampleData)
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