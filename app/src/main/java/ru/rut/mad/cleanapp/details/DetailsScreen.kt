package ru.rut.mad.cleanapp.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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

@Composable
fun DetailsScreen(
    // Теперь навигация не нужна, vm получаем из Koin
    viewModel: DetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is DetailsState.Content -> {
                Column {
                    AsyncImage(
                        model = currentState.element.image,
                        contentDescription = "Image of ${currentState.element.title}",
                        modifier = Modifier.size(200.dp),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_foreground)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "ID: ${currentState.element.id}")
                    Text(text = "Title: ${currentState.element.title}")
                    // Здесь можно добавить AsyncImage для картинки
                }
            }
            is DetailsState.Error -> {
                Text(text = currentState.message)
            }
            DetailsState.Loading -> {
                CircularProgressIndicator()
            }
        }
    }
}