package com.example.doggallery

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val temperament: String?,
    val origin: String?,
    val lifeSpan: String?,
    val description: String?,
    val imageUrl: String?,
    val isFavorite: Boolean = false
)