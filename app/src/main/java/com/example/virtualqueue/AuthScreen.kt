package com.example.virtualqueue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    state: AuthState,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String) -> Unit,
    startAsRegister: Boolean = false

) {

    var isLogin by remember { mutableStateOf(!startAsRegister) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isStaff by remember { mutableStateOf(false) }
    var staffCode by remember { mutableStateOf("") }
    val fieldShape = RoundedCornerShape(14.dp)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding()
    ) {
        // Gradient header
        Box(
            Modifier.fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(BrandStart, BrandEnd)),
                    RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = 28.dp, vertical = 44.dp)
        ) {
            Column {
                Box(
                    Modifier.size(64.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("⏱", fontSize = 30.sp) }
                Spacer(Modifier.height(16.dp))
                Text("WELCOME", color = Color.White,
                    style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Thank you for choosing virtual queue, osatopa pa line.",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyLarge)
            }
        }

        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                if (isLogin) "Welcome back" else "Create your account",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )

            if (!isLogin) {
                OutlinedTextField(
                    name, { name = it }, label = { Text("Full name") },
                    singleLine = true, shape = fieldShape, modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                email, { email = it }, label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true, shape = fieldShape, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                password, { password = it }, label = { Text("Password") },
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Hide" else "Show")
                    }
                },
                visualTransformation =
                    if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true, shape = fieldShape, modifier = Modifier.fillMaxWidth()
            )

            if (!isLogin) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isStaff, onCheckedChange = { isStaff = it })
                    Text("I'm a staff member")
                }
                if (isStaff) {
                    OutlinedTextField(
                        staffCode, { staffCode = it }, label = { Text("Staff access code") },
                        singleLine = true, shape = fieldShape, modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    if (isLogin) onLogin(email, password)
                    else onRegister(name, email, password, if (isStaff) staffCode else "")
                },
                enabled = !state.loading,
                shape = fieldShape,
                colors = ButtonDefaults.buttonColors(containerColor = BrandStart, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (state.loading) CircularProgressIndicator(
                    Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White
                ) else Text(if (isLogin) "Login" else "Register",
                    style = MaterialTheme.typography.titleMedium)
            }

            TextButton(onClick = { isLogin = !isLogin }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (isLogin) "No account? Register" else "Have an account? Login")
            }
        }
    }
}