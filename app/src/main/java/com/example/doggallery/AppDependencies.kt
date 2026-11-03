package com.example.doggallery

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AppDependencies {
    private lateinit var database: AppDatabase
    private lateinit var api: DogApi
    lateinit var repository: DogRepository
        private set

    fun init(context: Context) {
        if (::database.isInitialized) return

        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "dog_database"
        ).fallbackToDestructiveMigration().build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://api.thedogapi.com/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DogApi::class.java)

        repository = DogRepository(api, database.breedDao())
    }
}