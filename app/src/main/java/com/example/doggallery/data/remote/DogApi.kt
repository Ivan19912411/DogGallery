package com.example.doggallery.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

// Модели данных (DTO), которые приходят с сервера
@JsonClass(generateAdapter = true)
data class BreedDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "temperament") val temperament: String?,
    @Json(name = "origin") val origin: String?,
    @Json(name = "life_span") val lifeSpan: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "image") val image: ImageDto?
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    @Json(name = "url") val url: String?
)

// Интерфейс для Retrofit
interface DogApi {
    // Эндпоинт для получения списка пород
    @GET("breeds")
    suspend fun getBreeds(): List<BreedDto>
}