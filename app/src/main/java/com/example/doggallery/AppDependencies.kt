package com.example.doggallery

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

        // 👇 ЗДЕСЬ МЫ ДОБАВИЛИ ВАШ КЛЮЧ 👇
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("x-api-key", "live_Wm7PKlCpdb2iZe09VSOwvCEziFgv5bD9E6GkFmT6ty4d0kSEdVcnvF0XDAzCKxem")
                    .build()
                chain.proceed(request)
            }
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

class DogViewModelFactory(private val repository: DogRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}