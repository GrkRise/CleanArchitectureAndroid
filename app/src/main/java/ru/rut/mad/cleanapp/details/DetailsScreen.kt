package ru.rut.mad.cleanapp.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import ru.rut.mad.cleanapp.R
import ru.rut.mad.cleanapp.details.vm.DetailsState
import ru.rut.mad.cleanapp.details.vm.DetailsViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun DetailsScreen(
    // Теперь навигация не нужна, vm получаем из Koin
    viewModel: DetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState() // <-- СОБИРАЕМ state

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is DetailsState.Loading -> CircularProgressIndicator()
            is DetailsState.Error -> Text(text = currentState.message, color = Color.Red)
            is DetailsState.Content -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = currentState.element.image,
                        contentDescription = "Image of ${currentState.element.title}",
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // V-- ВЫЗЫВАЕМ МЕТОД viewModel --V
                    Button(onClick = { viewModel.applyFilter() }) {
                        Text("Применить Ч/Б фильтр (нужна зарядка)")
                    }
                    // ^-- ВЫЗЫВАЕМ МЕТОД viewModel --^

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "ID: ${currentState.element.id}")
                    Text(text = "Title: ${currentState.element.title}")
                }
            }
        }
    }
}