@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uthsmarttasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// ---------- OOP models ----------
data class OnboardingItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

object OnboardingRepository {
    // 3 màn
    fun items() = listOf(
        OnboardingItem(
            imageRes = R.drawable.logo1,
            title = "Easy Time Management",
            description = "With management based on priority and daily tasks, it gives you convenience in managing and determining the tasks that must be done first."
        ),
        OnboardingItem(
            imageRes = R.drawable.logo2,
            title = "Increase Work Effectiveness",
            description = "Time management and statistics help you focus on important work first and always improve."
        ),
        OnboardingItem(
            imageRes = R.drawable.logo3,
            title = "Reminder Notification",
            description = "Never forget deadlines. Get on-time reminders according to your schedules."
        )
    )
}

// ---------- Navigation routes ----------
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding/{index}") {
        fun routeOf(index: Int) = "onboarding/$index"
    }
    data object Home : Screen("home")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onDone = {
                                navController.navigate(Screen.Onboarding.routeOf(0)) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        Screen.Onboarding.route,
                        arguments = listOf(navArgument("index") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val index = backStackEntry.arguments?.getInt("index") ?: 0
                        OnboardingScreen(
                            index = index,
                            items = OnboardingRepository.items(),
                            onBack = { prev ->
                                if (prev >= 0)
                                    navController.navigate(Screen.Onboarding.routeOf(prev)) {
                                        popUpTo(Screen.Onboarding.routeOf(prev)) { inclusive = true }
                                    }
                            },
                            onNext = { next ->
                                if (next <= OnboardingRepository.items().lastIndex)
                                    navController.navigate(Screen.Onboarding.routeOf(next)) {
                                        popUpTo(Screen.Onboarding.routeOf(next)) { inclusive = true }
                                    }
                            },
                            onSkip = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                            onFinish = { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                        )
                    }

                    composable(Screen.Home.route) { HomeScreen() }
                }
            }
        }
    }
}

// ---------- Splash with network image ----------
@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500) // 1.5s
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(24.dp)),
                model = "https://tuyensinh.ut.edu.vn/wp-content/uploads/2022/03/275244853_4747835955339153_6364076342905238115_n-300x300.jpg",
                contentDescription = "UTH Logo",
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "UTH SmartTasks",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ---------- Reusable Onboarding screen ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    index: Int,
    items: List<OnboardingItem>,
    onBack: (prevIndex: Int) -> Unit,
    onNext: (nextIndex: Int) -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    val item = items[index]
    val isFirst = index == 0
    val isLast = index == items.lastIndex

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Get Started${if (!isLast) " (${index + 1}/${items.size})" else ""}")
                },
                actions = {
                    if (!isLast) TextButton(onClick = onSkip) { Text("Skip") }
                }
            )
        },
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                DotsIndicator(total = items.size, selectedIndex = index)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleBackButton(
                        enabled = !isFirst,
                        onClick = { onBack(index - 1) }
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = { if (isLast) onFinish() else onNext(index + 1) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (isLast) "Get Started" else "Next")
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(26.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = item.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = item.description,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CircleBackButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bg = if (enabled) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(bg)
            .let { if (enabled) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun DotsIndicator(total: Int, selectedIndex: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val active = i == selectedIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (active) 10.dp else 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

// ---------- Home placeholder ----------
@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Home Screen", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    }
}
