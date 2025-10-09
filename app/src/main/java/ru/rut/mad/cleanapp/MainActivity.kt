package ru.rut.mad.cleanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import ru.rut.mad.cleanapp.details.DetailsScreen
import ru.rut.mad.cleanapp.details.DetailsScreenRoute
import ru.rut.mad.cleanapp.main.MainScreen
import ru.rut.mad.cleanapp.main.MainScreenRoute
import ru.rut.mad.cleanapp.main.vm.MainViewModel
import ru.rut.mad.cleanapp.ui.theme.CleanAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // ИЗМЕНЕНИЕ: Настраиваем NavHost с двумя экранами
                    NavHost(navController = navController, startDestination = MainScreenRoute) {
                        composable<MainScreenRoute> {
                            val viewModel: MainViewModel = koinViewModel()
                            val state by viewModel.state.collectAsState()

                            // ИЗМЕНЕНИЕ: Слушаем навигационные события от ViewModel
                            LaunchedEffect(Unit) {
                                viewModel.navigationEvent.collect { elementId ->
                                    navController.navigate(DetailsScreenRoute(id = elementId.toString()))
                                }
                            }

                            // ИЗМЕНЕНИЕ: Передаем состояние и лямбду, а не NavController
                            MainScreen(
                                state = state,
                                onElementClick = { elementId ->
                                    viewModel.onElementClick(elementId.toString())
                                }
                            )
                        }
                        composable<DetailsScreenRoute> {
                            DetailsScreen() // Вызываем экран деталей
                        }
                    }
                }
            }
        }
    }
}