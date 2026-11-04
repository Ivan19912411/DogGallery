package com.example.doggallery

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DogRepository(
    private val api: DogApi,
    private val dao: BreedDao
) {
    fun getBreeds(query: String): Flow<List<BreedEntity>> = dao.getBreeds(query)
    fun getFavorites(): Flow<List<BreedEntity>> = dao.getFavorites()
    fun getBreedById(id: Int): Flow<BreedEntity?> = dao.getBreedById(id)

    suspend fun refreshBreeds() {
        val remoteBreeds = api.getBreeds()

        // 👇 ФИКС: Получаем список ID тех, кто уже в избранном 👇
        val favoriteIds = dao.getFavorites().first().map { it.id }.toSet()

        val entities = remoteBreeds.map { dto ->
            BreedEntity(
                id = dto.id,
                name = dto.name,
                temperament = dto.temperament,
                origin = dto.origin,
                lifeSpan = dto.lifeSpan,
                description = dto.description,
                imageUrl = dto.image?.url,
                // 👇 Если собака уже была в избранном - сохраняем true, иначе false 👇
                isFavorite = favoriteIds.contains(dto.id)
            )
        }
        dao.insertBreeds(entities)
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite)
    }
}