package com.example.virtualqueue

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Notifier.createChannel(this)
        setContent {
            QueueTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val vm: AuthViewModel = viewModel()
                val state by vm.state.collectAsState()
                val user = state.user
                var showAuth by rememberSaveable { mutableStateOf(false) }
                var startRegister by rememberSaveable { mutableStateOf(true) }

                if (user == null) {
                    BackHandler(enabled = showAuth) { showAuth = false }
                    if (!showAuth) {
                        LandingScreen(
                            onGetStarted = { startRegister = true; showAuth = true },
                            onLogin = { startRegister = false; showAuth = true }
                        )
                    } else {
                        Box(Modifier.fillMaxSize()) {
                            AuthScreen(state, vm::login, vm::register, startAsRegister = startRegister)
                            TextButton(
                                onClick = { showAuth = false },
                                modifier = Modifier.statusBarsPadding().align(Alignment.TopStart),
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                            ) { Text("← Back") }
                        }
                    }
                } else {
                    val logout: () -> Unit = { vm.logout(); showAuth = false }
                    val queueVm: QueueViewModel = viewModel(key = user.uid)
                    if (user.role.trim().lowercase() == "staff" || user.role.trim().lowercase() == "admin") {
                        StaffScreen(user, queueVm, logout)
                    } else {
                        CustomerScreen(user, queueVm, logout)
                    }
                }
            }
        }
    }
}