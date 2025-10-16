package com.example.uicomponentsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.uicomponentsdemo.navigation.NavGraph
import com.example.uicomponentsdemo.ui.theme.UIComponentsDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UIComponentsDemoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph() // Gọi hàm điều hướng chính
                }
            }
        }
    }
}
