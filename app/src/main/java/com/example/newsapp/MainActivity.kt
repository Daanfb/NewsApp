package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.view.WindowCompat
import com.example.newsapp.ui.core.navigation.NavigationWrapper
import com.example.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        setContent {

            WindowCompat.getInsetsController(window, window.decorView)
                .isAppearanceLightStatusBars = isSystemInDarkTheme().not()

            NewsAppTheme(dynamicColor = false) {
                NavigationWrapper()
            }
        }
    }
}