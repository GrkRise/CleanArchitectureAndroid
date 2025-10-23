package ru.rut.mad.cleanapp.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rut.mad.domain.entity.ListElementEntity
import ru.rut.mad.domain.usecase.GetCatsUseCase

class MainViewModel(
    private val getCatsUseCase: GetCatsUseCase
) : ViewModel() {

    /**
     * StateFlow для состояния UI.
     * 1. Вызываем getElementsUseCase(), который возвращает Flow<List<...>>.
     * 2. С помощью .map преобразуем каждый новый список в соответствующий MainState.
     *    Если список пуст (что возможно при первом запуске, пока данные не загрузились), показываем Loading.
     * 3. С помощью .catch ловим любые ошибки во Flow и преобразуем их в MainState.Error.
     * 4. С помощью .stateIn преобразуем "холодный" Flow в "горячий" StateFlow,
     *    который кэширует последнее значение и доступен для UI.
     */
    val state: StateFlow<MainState> = getCatsUseCase.execute(Unit)
        .map<List<ListElementEntity>, MainState> { list ->
            if (list.isEmpty()) {
                MainState.Loading
            } else {
                MainState.Content(list)
            }
        }
        .catch { e ->
            emit(MainState.Error(e.localizedMessage ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainState.Loading
        )

    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()


    // Метод, который будет вызывать UI
    fun onElementClick(elementId: String) {
        viewModelScope.launch {
            // Отправляем событие навигации в канал
            _navigationEvent.send(elementId)
        }
    }
}