package com.example.uicomponentsdemo.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uicomponentsdemo.screens.*

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "intro") {

        composable("intro") { IntroScreen(navController) }

        composable("list") { ComponentListScreen(navController) }

        // Route động: mở các màn hình chi tiết dựa trên "type"
        composable("detail/{type}") { backStackEntry ->
            when (backStackEntry.arguments?.getString("type")) {
                "Text" -> DetailTextScreen(navController)
                "Image" -> DetailImageScreen(navController)
                "TextField" -> DetailTextFieldScreen(navController)
                "Row" -> DetailRowScreen(navController)
                // Nếu nhấn vào mục chưa có màn hình cụ thể:
                else -> Text("This component detail is not available yet.")
            }
        }
    }
}
