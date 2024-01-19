package com.example.compose_arch_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose_arch_sample.first_screen.FirstScreen
import com.example.compose_arch_sample.second_screen.SecondScreen
import com.example.compose_arch_sample.ui.theme.ComposeArchSampleTheme

class RootActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeArchSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationHost()
                }
            }
        }
    }

    @Composable
    private fun NavigationHost() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "firstscreen"
        ) {
            composable("firstscreen") {
                FirstScreen(navController)
            }
            composable("secondscreen") {
                SecondScreen(navController)
            }
        }
    }
}