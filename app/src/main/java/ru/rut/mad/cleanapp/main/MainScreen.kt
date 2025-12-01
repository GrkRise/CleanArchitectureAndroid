package ru.rut.mad.cleanapp.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rut.mad.cleanapp.R
import ru.rut.mad.cleanapp.main.vm.MainState
import ru.rut.mad.cleanapp.main.vm.PlayerState
import ru.rut.mad.cleanapp.ui.theme.CleanAppTheme

import ru.rut.mad.domain.entity.ListElementEntity

// ИЗМЕНЕНИЕ: MainScreen теперь "глупый"
@Composable
fun MainScreen(
    state: MainState,
    playerState: PlayerState,      // Данные из Плеера
    onPlayPauseClick: (String) -> Unit, // Клик по кнопке Play
    onElementClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is MainState.Content -> {
                ContentState(
                    list = state.list,
                    currentTrackIndex = playerState.currentTrackIndex,
                    isPlaying = playerState.isPlaying,
                    onPlayPauseClick = onPlayPauseClick,
                    onElementClick = onElementClick
                )
            }
            is MainState.Error -> ErrorState(message = state.message)
            MainState.Loading -> LoadingState()
        }
    }
}

@Composable
fun ContentState(
    list: List<ListElementEntity>,
    currentTrackIndex: Int,
    isPlaying: Boolean,
    onPlayPauseClick: (String) -> Unit,
    onElementClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Используем itemsIndexed, чтобы сопоставить индекс списка с индексом плеера
        itemsIndexed(list) { index, element ->

            // Логика отображения: так как треков всего 2, мы используем остаток от деления.
            // Элементы 0, 2, 4... управляют треком 1 (индекс 0)
            // Элементы 1, 3, 5... управляют треком 2 (индекс 1)
            val isLinkedToCurrentTrack = (index % 2 == currentTrackIndex)
            val showPauseIcon = isLinkedToCurrentTrack && isPlaying

            ElementRow(
                element = element,
                showPauseIcon = showPauseIcon,
                onPlayClick = { onPlayPauseClick(element.id) },
                onItemClick = { onElementClick(element.id) }
            )
        }
    }
}

@Composable
fun ElementRow(
    element: ListElementEntity,
    showPauseIcon: Boolean,
    onPlayClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onItemClick // Клик по карточке ведет к навигации
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Отдельная кнопка Play/Pause
            IconButton(
                onClick = onPlayClick, // Клик по кнопке управляет музыкой
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (showPauseIcon) R.drawable.ic_pause else R.drawable.ic_play_arrow
                    ),
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Текст и картинка
            Column {
                Text(text = element.title, style = MaterialTheme.typography.titleMedium)
                // AsyncImage можно добавить сюда же, как в прошлых лабах
            }
        }
    }
}

@Composable
fun LoadingState() {
    CircularProgressIndicator()
}

@Composable
fun ErrorState(message: String) {
    Text(text = message, color = MaterialTheme.colorScheme.error)
}

// --- ИНСТРУМЕНТЫ ДЛЯ ПРЕВЬЮ ---

private val sampleDataForPreview = listOf(
    ListElementEntity("1", "Cool Cat", "https://cataas.com/cat/says/hello", true),
    ListElementEntity("2", "Serious Cat", "https://cataas.com/cat", false),
    ListElementEntity("3", "Cute Cat", "https://cataas.com/cat/cute", true)
)

@Preview(showBackground = true)
@Composable
fun ContentStatePreview() {
    // Для превью нам не нужны настоящие данные из ViewModel
    val sampleData = listOf(
        ListElementEntity("1", "Cool Cat", "...", true),
        ListElementEntity("2", "Serious Cat", "...", false)
    )
    // Передаем в превью пустую лямбду, так как навигация здесь не нужна
    ContentState(
        list = sampleData, onElementClick = {},
        currentTrackIndex = TODO(),
        isPlaying = TODO(),
        onPlayPauseClick = TODO()
    )
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun LoadingStatePreview() {
    CleanAppTheme {
        Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // И его тоже вызываем напрямую
                LoadingState()
            }
        }
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
fun ErrorStatePreview() {
    CleanAppTheme {
        Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ErrorState(message = "Something went wrong!")
            }
        }
    }
}