package ru.rut.mad.cleanapp.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.rut.mad.cleanapp.ui.theme.CleanAppTheme
import ru.rut.mad.cleanapp.ui.view.Like
import ru.rut.mad.domain.entity.ListElementEntity

// Наша цель - создать компонент, который просто отображает данные.
@Composable
fun MainScreen() {
    // Пока что мы отображаем только одно состояние - Content.
    // Логику загрузки и ошибок добавим в следующем задании.
    ContentState(list = sampleData)
}

@Composable
fun ContentState(list: List<ListElementEntity>) {
    // Используем LazyColumn для эффективного отображения списков
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) { element ->
            ElementRow(element = element)
        }
    }
}

@Composable
fun ElementRow(element: ListElementEntity) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier.size(100.dp), // Уменьшим размер для превью
            model = element.image,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = element.title,
            modifier = Modifier.weight(1f) // Занимает все оставшееся место
        )
        Spacer(modifier = Modifier.width(16.dp))
        Like(isLiked = element.like)
    }
}

// --- ПРИМЕР ДАННЫХ ДЛЯ ПРЕВЬЮ ---
private val sampleData = listOf(
    ListElementEntity("1", "Cool Cat", "https://cataas.com/cat/says/hello", true),
    ListElementEntity("2", "Serious Cat", "https://cataas.com/cat", false),
    ListElementEntity("3", "Cute Cat", "https://cataas.com/cat/cute", true)
)

// --- ФУНКЦИЯ ДЛЯ ПРЕВЬЮ В ANDROID STUDIO ---
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CleanAppTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MainScreen()
        }
    }
}