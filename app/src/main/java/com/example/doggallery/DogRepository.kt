package com.example.doggallery

import kotlinx.coroutines.flow.Flow

class DogRepository(
    private val api: DogApi,
    private val dao: BreedDao
) {
    fun getBreeds(query: String): Flow<List<BreedEntity>> = dao.getBreeds(query)
    fun getFavorites(): Flow<List<BreedEntity>> = dao.getFavorites()
    fun getBreedById(id: Int): Flow<BreedEntity?> = dao.getBreedById(id)

    suspend fun refreshBreeds() {
        // Убрали try-catch, чтобы ошибка передалась в ViewModel
        val remoteBreeds = api.getBreeds()
        val entities = remoteBreeds.map { dto ->
            BreedEntity(
                id = dto.id,
                name = dto.name,
                temperament = dto.temperament,
                origin = dto.origin,
                lifeSpan = dto.lifeSpan,
                description = dto.description,
                imageUrl = dto.image?.url,
                isFavorite = false
            )
        }
        dao.insertBreeds(entities)
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite)
    }
}