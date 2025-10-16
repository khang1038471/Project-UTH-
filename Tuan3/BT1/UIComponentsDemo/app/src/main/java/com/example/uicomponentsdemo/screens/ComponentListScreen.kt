package com.example.uicomponentsdemo.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ComponentListScreen(navController: NavController) {
    val components = listOf(
        "Text" to "Displays text",
        "Image" to "Displays an image",
        "TextField" to "Input field for text",
        "PasswordField" to "Input field for passwords",
        "Column" to "Arranges elements vertically",
        "Row" to "Arranges elements horizontally"
    )

    Column(Modifier.padding(16.dp)) {
        Text(text = "UI Components List")
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(components.size) { index ->
                val (title, desc) = components[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { navController.navigate("detail/$title") }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(text = title)
                        Text(text = desc)
                    }
                }
            }
        }
    }
}
