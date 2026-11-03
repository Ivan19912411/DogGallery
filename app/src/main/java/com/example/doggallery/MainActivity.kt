package com.example.doggallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.doggallery.ui.theme.DogGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 👇 ИНИЦИАЛИЗИРУЕМ РУЧНОЙ DI (БД и Сеть) 👇
        AppDependencies.init(applicationContext)

        setContent {
            DogGalleryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Пока здесь заглушка, на следующем шаге мы добавим сюда NavDisplay
                    Text(
                        text = "Dog Gallery загружается...",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}