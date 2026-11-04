package com.example.doggallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DogViewModel,
    onBreedClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit
) {
    val breeds by viewModel.breeds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Автоматическая загрузка при запуске
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Породы собак") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Избранное")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Поле поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                placeholder = { Text("Поиск породы...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                // Если произошла ошибка сети - показываем её и кнопку повтора
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Ошибка: $error", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Повторить")
                    }
                }
            } else if (breeds.isEmpty()) {
                // Если данных нет (например, API вернул пустой список)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Список пуст или нет интернета")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Обновить данные")
                    }
                }
            } else {
                // Список собак
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(breeds) { breed ->
                        BreedCard(breed = breed, onClick = { onBreedClick(breed.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun BreedCard(breed: BreedEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (breed.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(breed.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = breed.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Text("Нет фото")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = breed.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = breed.temperament ?: "Характер неизвестен", maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DogViewModel,
    breedId: Int,
    onBack: () -> Unit
) {
    val breedFlow = remember(breedId) { viewModel.repository.getBreedById(breedId) }
    val breed by breedFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(breed?.name ?: "Загрузка...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (breed == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val b = breed!!
            Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                if (b.imageUrl != null) {
                    AsyncImage(
                        model = b.imageUrl,
                        contentDescription = b.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = b.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { viewModel.toggleFavorite(b.id, b.isFavorite) }) {
                        Icon(
                            imageVector = if (b.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "В избранное",
                            tint = if (b.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Происхождение: ${b.origin ?: "Неизвестно"}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Продолжительность жизни: ${b.lifeSpan ?: "Неизвестно"}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = b.description ?: "Описание отсутствует", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: DogViewModel,
    onBreedClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет избранных пород")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(favorites) { breed ->
                    BreedCard(breed = breed, onClick = { onBreedClick(breed.id) })
                }
            }
        }
    }
}