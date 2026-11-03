package com.example.doggallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DogViewModel(private val repository: DogRepository) : ViewModel() {

    // Состояние поискового запроса
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Список пород (реактивно обновляется при изменении запроса или БД)
    @OptIn(ExperimentalCoroutinesApi::class)
    val breeds = _searchQuery
        .flatMapLatest { query ->
            repository.getBreeds(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Список избранного
    val favorites = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // Обновление данных из сети (Pull-to-refresh или первый запуск)
    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshBreeds()
            } catch (e: Exception) {
                _error.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Переключение избранного
    fun toggleFavorite(id: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !currentStatus)
        }
    }
}