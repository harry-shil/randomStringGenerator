package com.example.randomstringgenerator

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.randomstringgenerator.ui.screen.RandomStringScreen
import com.example.randomstringgenerator.ui.theme.RandomStringGeneratorTheme

class MainActivity : ComponentActivity() {
    private val viewModel: RandomStringViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RandomStringGeneratorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RandomStringScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
