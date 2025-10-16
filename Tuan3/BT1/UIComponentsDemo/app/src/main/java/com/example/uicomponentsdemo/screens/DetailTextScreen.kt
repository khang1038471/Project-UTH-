package com.example.uicomponentsdemo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTextScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Text Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text(
                text = buildAnnotatedString {
                    append("The ")
                    pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    append("quick ")
                    pop()
                    pushStyle(SpanStyle(color = Color(0xFF795548), fontWeight = FontWeight.Bold))
                    append("Brown ")
                    pop()
                    append("fox j u m p s ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append("over ")
                    pop()
                    pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                    append("the lazy")
                    pop()
                    append(" dog.")
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
