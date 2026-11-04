package com.example.doggallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.doggallery.ui.theme.DogGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppDependencies.init(applicationContext)

        setContent {
            DogGalleryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val viewModel: DogViewModel = viewModel(
        factory = DogViewModelFactory(AppDependencies.repository)
    )

    val backStack = rememberNavBackStack(HomeRoute)
    val onNavigation: (NavKey) -> Unit = { backStack.add(it) }
    val onBack: () -> Unit = { backStack.removeLastOrNull() }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        // 👇 ВОТ ЗДЕСЬ ИСПРАВЛЕНИЕ (добавили { _ -> ... }) 👇
        onBack = { _ -> backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    viewModel = viewModel,
                    onBreedClick = { id -> onNavigation(DetailRoute(id)) },
                    onFavoritesClick = { onNavigation(FavoritesRoute) }
                )
            }
            entry<DetailRoute> { key ->
                DetailScreen(
                    viewModel = viewModel,
                    breedId = key.breedId,
                    onBack = onBack
                )
            }
            entry<FavoritesRoute> {
                FavoritesScreen(
                    viewModel = viewModel,
                    onBreedClick = { id -> onNavigation(DetailRoute(id)) },
                    onBack = onBack
                )
            }
        }
    )
}