package com.example.virtualqueue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingScreen(onGetStarted: () -> Unit, onLogin: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        // Hero
        Box(
            Modifier.fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(BrandStart, BrandEnd)),
                    RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = 28.dp, vertical = 48.dp)
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(88.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("⏱", fontSize = 54.sp) }
                Spacer(Modifier.height(20.dp))
                Text(
                    "Virtual Queue", color = Color.White,
                    style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "We'll tell you when it's your turn. Don't force yourself to be on line",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))
                Button(
                    onClick = onGetStarted,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = BrandStart),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) { Text("Get started", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                TextButton(
                    onClick = onLogin,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) { Text("I already have an account") }
            }
        }

        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Why Virtual Queue?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            FeatureCard("📱", "Join From Anywhere", "Use your phone. No need to stand in a physical line.")
            FeatureCard("", "Live position and wait time", "See how many people are ahead of you and roughly how long it will take.")
            FeatureCard("🔔", "Get notified", "Use other apps while you wait. We alert you when you're close and when it's your turn.")
            FeatureCard("💬", "Message the staff", "Staff can send you a message, and you can reply right from the app.")

            Spacer(Modifier.height(8.dp))
            Text("How it works", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Step("1", "Create an account", "Register in under a minute.")
            Step("2", "Join a queue", "Pick the service you need and get your ticket number.")
            Step("3", "Wait anywhere", "Watch your position, or just wait for the notification.")
            Step("4", "Walk up when called", "Go to the counter when it's your turn.")

            Spacer(Modifier.height(12.dp))

            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun FeatureCard(emoji: String, title: String, text: String) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(48.dp).background(BrandStart.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 24.sp) }
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun Step(number: String, title: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(36.dp).background(BrandStart, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(number, color = Color.White, fontWeight = FontWeight.Bold) }
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}