@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dataflownav

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent as AndroidKeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import java.io.Serializable

// OTP imports
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.selection.DisableSelection

// ---------- result để hiện tóm tắt ở màn đầu ----------
data class FlowResult(val email: String, val code: String, val password: String) : Serializable

// ---------- Routes ----------
sealed class Screen(val route: String) {
    data object Forgot : Screen("forgot")
    data object Verify : Screen("verify/{email}") {
        fun of(email: String) = "verify/${Uri.encode(email)}"
    }
    data object Reset : Screen("reset/{email}/{code}") {
        fun of(email: String, code: String) =
            "reset/${Uri.encode(email)}/${Uri.encode(code)}"
    }
    data object Confirm : Screen("confirm/{email}/{code}/{password}") {
        fun of(email: String, code: String, password: String) =
            "confirm/${Uri.encode(email)}/${Uri.encode(code)}/${Uri.encode(password)}"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                val nav = rememberNavController()

                NavHost(navController = nav, startDestination = Screen.Forgot.route) {

                    // 1) Forgot
                    composable(Screen.Forgot.route) {
                        val resultState = nav.currentBackStackEntry
                            ?.savedStateHandle
                            ?.getStateFlow<FlowResult?>("flow_result", null)
                        val result by (resultState?.collectAsState() ?: remember { mutableStateOf(null) })

                        ForgotPasswordScreen(
                            result = result,
                            onNext = { email -> nav.navigate(Screen.Verify.of(email)) }
                        )
                    }

                    // 2) Verify
                    composable(
                        Screen.Verify.route,
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { back ->
                        val email = back.arguments?.getString("email").orEmpty()
                        VerifyCodeScreen(
                            email = email,
                            onBack = { nav.popBackStack() },
                            onNext = { code -> nav.navigate(Screen.Reset.of(email, code)) }
                        )
                    }

                    // 3) Reset
                    composable(
                        Screen.Reset.route,
                        arguments = listOf(
                            navArgument("email") { type = NavType.StringType },
                            navArgument("code") { type = NavType.StringType }
                        )
                    ) { back ->
                        val email = back.arguments?.getString("email").orEmpty()
                        val code = back.arguments?.getString("code").orEmpty()
                        ResetPasswordScreen(
                            onBack = { nav.popBackStack() },
                            onNext = { password ->
                                nav.navigate(Screen.Confirm.of(email, code, password))
                            }
                        )
                    }

                    // 4) Confirm
                    composable(
                        Screen.Confirm.route,
                        arguments = listOf(
                            navArgument("email") { type = NavType.StringType },
                            navArgument("code") { type = NavType.StringType },
                            navArgument("password") { type = NavType.StringType }
                        )
                    ) { back ->
                        val email = back.arguments?.getString("email").orEmpty()
                        val code = back.arguments?.getString("code").orEmpty()
                        val password = back.arguments?.getString("password").orEmpty()

                        ConfirmScreen(
                            email = email,
                            code = code,
                            password = password,
                            onBack = { nav.popBackStack() },
                            onSubmit = {
                                nav.getBackStackEntry(Screen.Forgot.route)
                                    .savedStateHandle["flow_result"] =
                                    FlowResult(email, code, password)
                                nav.popBackStack(Screen.Forgot.route, false)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ---------- Common UI ----------
@Composable
fun AppLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(16.dp)),
            model = "https://tuyensinh.ut.edu.vn/wp-content/uploads/2022/03/275244853_4747835955339153_6364076342905238115_n-300x300.jpg",
            contentDescription = "UTH Logo",
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text("SmartTasks", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun BackOnlyScaffold(
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = { /* no title */ },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        },
        content = content
    )
}

// ---------- OTP 6 ô: nhập trực tiếp, bàn phím số, auto focus ----------
@Composable
fun OtpInput(
    length: Int = 6,
    modifier: Modifier = Modifier,
    onCodeChange: (String) -> Unit,
    onFilled: (String) -> Unit
) {
    // dùng SnapshotStateList để trigger recomposition khi thay từng phần tử
    val values = remember { mutableStateListOf<String>().apply { repeat(length) { add("") } } }
    val focusManager = LocalFocusManager.current
    val requesters = remember { List(length) { FocusRequester() } }

    // focus ô đầu khi composable xuất hiện
    LaunchedEffect(Unit) {
        requesters.getOrNull(0)?.requestFocus()
    }

    fun codeNow() = values.joinToString("")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { i ->
            // Tắt selection highlight để tránh caret kỳ lạ trên một số devices
            DisableSelection {
                OutlinedTextField(
                    value = values[i],
                    onValueChange = { txt ->
                        val filtered = txt.filter(Char::isDigit).take(1) // 1 số/ô
                        values[i] = filtered
                        val code = codeNow()
                        onCodeChange(code)

                        if (filtered.isNotEmpty()) {
                            if (i < length - 1) requesters[i + 1].requestFocus()
                            else {
                                focusManager.clearFocus()
                                onFilled(code)
                            }
                        }
                    },
                    singleLine = true,
                    // đảm bảo chữ hiện rõ bằng cách đặt màu trong textStyle
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .focusRequester(requesters[i])
                        .onKeyEvent { ev ->
                            // dùng nativeKeyEvent để detect backspace (KEYCODE_DEL)
                            val code = ev.nativeKeyEvent.keyCode
                            if (code == AndroidKeyEvent.KEYCODE_DEL && values[i].isEmpty() && i > 0) {
                                requesters[i - 1].requestFocus()
                                values[i - 1] = ""
                                onCodeChange(codeNow())
                                true
                            } else false
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (i == length - 1) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { if (i < length - 1) requesters[i + 1].requestFocus() },
                        onDone = {
                            val code = codeNow()
                            if (code.length == length) onFilled(code)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

// ---------- 1) Forgot----------
@Composable
fun ForgotPasswordScreen(
    result: FlowResult?,
    onNext: (email: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    Scaffold { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            Spacer(Modifier.height(28.dp))
            Text("Forget Password?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Enter your Email, we will send you a verification code.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
            )

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text("Your Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onNext(email) },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Next") }

            if (result != null) {
                Spacer(Modifier.height(24.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Text("Last submission:", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Email: ${result.email}")
                    Text("Code : ${result.code}")
                    Text("Password: ${result.password}")
                }
            }
        }
    }
}

// ---------- 2) Verify----------
@Composable
fun VerifyCodeScreen(
    email: String,
    onBack: () -> Unit,
    onNext: (code: String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    BackOnlyScaffold(onBack = onBack) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            Spacer(Modifier.height(28.dp))
            Text("Verify Code", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Enter the code we just sent to your registered Email\n$email",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
            )

            Spacer(Modifier.height(16.dp))

            // <-- DÙNG OtpInput: nhập trực tiếp lên 6 ô, bàn phím số -->
            OtpInput(
                length = 6,
                modifier = Modifier.fillMaxWidth(),
                onCodeChange = { code = it },
                onFilled = { filled -> code = filled } // auto-fill khi đủ
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onNext(code) },
                enabled = code.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Next") }
        }
    }
}

// ---------- 3) Reset password----------
@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onNext: (password: String) -> Unit
) {
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val ok = pass.length >= 6 && pass == confirm

    BackOnlyScaffold(onBack = onBack) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            Spacer(Modifier.height(28.dp))
            Text("Create new password", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Your new password must be different from previously used password",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                singleLine = true,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Hide" else "Show"
                        )
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                singleLine = true,
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Hide" else "Show"
                        )
                    }
                }
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onNext(pass) },
                enabled = ok,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Next") }
        }
    }
}

// ---------- 4) Confirm----------
@Composable
fun ConfirmScreen(
    email: String,
    code: String,
    password: String,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    BackOnlyScaffold(onBack = onBack) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            Spacer(Modifier.height(28.dp))
            Text("Confirm", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("We are here to help you!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f))

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = email, onValueChange = {}, readOnly = true,
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = code, onValueChange = {}, readOnly = true,
                label = { Text("Code") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = "*".repeat(password.length), onValueChange = {}, readOnly = true,
                label = { Text("Password") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Submit") }
        }
    }
}
