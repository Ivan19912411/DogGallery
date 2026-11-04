package com.example.doggallery

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data class DetailRoute(val breedId: Int) : NavKey

@Serializable
data object FavoritesRoute : NavKey