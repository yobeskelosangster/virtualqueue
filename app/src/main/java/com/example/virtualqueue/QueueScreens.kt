package com.example.virtualqueue

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay


@Composable
private fun GradientHeader(title: String, subtitle: String, onLogout: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(BrandStart, BrandEnd)),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 22.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(subtitle, color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium)
                Text(title, color = Color.White,
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = onLogout,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) { Text("Logout") }
        }
    }
}

@Composable
private fun cardColors() =
    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)

@Composable
private fun Avatar(text: String) {
    Box(
        Modifier.size(44.dp).background(BrandStart.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(text, Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun EmptyState(emoji: String, text: String) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 44.sp)
        Spacer(Modifier.height(8.dp))
        Text(text, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


@Composable
private fun WaitingCard(
    userName: String,
    ticket: Ticket,
    ahead: Int,
    avgMinutes: Long,
    onLeave: () -> Unit
) {
    Card(Modifier.fillMaxWidth(), colors = cardColors()) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hi, $userName", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold)
            Text("Thank you for waiting.", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("•  •  •", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Here's your position in the queue:",
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))

            Box(
                Modifier.size(210.dp)
                    .background(Brush.linearGradient(listOf(BrandStart, BrandEnd)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${ahead + 1}", color = Color.White, fontSize = 84.sp,
                        fontWeight = FontWeight.ExtraBold)
                    Text("position", color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(Modifier.height(18.dp))
            Chip("Ticket #${ticket.number} · ${ticket.queueName}")
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                Stat("$ahead", "People ahead", Modifier.weight(1f))
                Stat("${ahead * avgMinutes} min", "Est. wait", Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Text("We'll notify you when we're almost ready to see you. " +
                    "You can use other apps while you wait.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onLeave,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Leave queue") }
        }
    }
}

@Composable
fun CustomerScreen(user: AppUser, vm: QueueViewModel, onLogout: () -> Unit) {
    val queues by vm.queues.collectAsState()
    val ticket by vm.myTicket.collectAsState()
    val ahead by vm.peopleAhead.collectAsState()
    val message by vm.message.collectAsState()
    val myMessages by vm.myMessages.collectAsState()
    val context = LocalContext.current

    var pendingQueue by remember { mutableStateOf<QueueInfo?>(null) }
    val startTime = remember { System.currentTimeMillis() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        pendingQueue?.let { vm.join(context, it, user) }
        pendingQueue = null
    }

    fun needsNotificationPermission() =
        Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED

    // "It's your turn" alert
    LaunchedEffect(ticket?.id, ticket?.status) {
        val t = ticket
        if (t != null && t.status == "called") {
            Notifier.show(
                context, 2, "It's your turn!",
                "Ticket #${t.number} at ${t.queueName}. Please proceed to the counter."
            )
        }
    }

    LaunchedEffect(ticket?.id, ticket?.status, ahead) {
        val t = ticket
        if (t != null && t.status == "waiting") {
            delay(800)
            if (ahead == 0) {
                Notifier.show(context, 1, "You're next!", "Get ready, ${t.queueName} will call you soon.")
            } else if (ahead <= 2) {
                Notifier.show(context, 1, "Almost your turn", "You are number ${ahead + 1} in line at ${t.queueName}.")
            }
        }
    }
    LaunchedEffect(myMessages.size) {
        val last = myMessages.lastOrNull()
        if (last != null && last.fromStaff && last.sentAt > startTime) {
            Notifier.show(context, 3, "Message from ${last.fromName}", last.text)
        }
    }

    if (pendingQueue != null) {
        AlertDialog(
            onDismissRequest = { pendingQueue = null },
            title = { Text("Allow notifications?") },
            text = {
                Text("Turn on notifications so you can use other apps while you wait. " +
                        "We'll alert you when you're close to the front, when it's your turn, " +
                        "and when staff send you a message.")
            },
            confirmButton = {
                TextButton(onClick = {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text("Allow and join") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingQueue?.let { vm.join(context, it, user) }
                    pendingQueue = null
                }) { Text("Join without alerts") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        GradientHeader(user.name, "Hello,", onLogout)

        LazyColumn(
            Modifier.weight(1f).imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            message?.let {
                item { Text(it, color = MaterialTheme.colorScheme.error) }
            }

            val t = ticket
            if (t != null) {
                if (t.status == "called") {
                    item {
                        Card(
                            Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = BrandStart)
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎉", fontSize = 44.sp)
                                Text("It's your turn!", color = Color.White,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text("Ticket #${t.number} · ${t.queueName}",
                                    color = Color.White.copy(alpha = 0.9f))
                                Text("Please proceed to the counter.",
                                    color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                    }
                } else {
                    item {
                        val avg = queues.find { it.id == t.queueId }?.avgServiceMinutes ?: 5L
                        WaitingCard(user.name, t, ahead, avg) { vm.cancel(t.id) }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth(), colors = cardColors()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Messages", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold)
                            ChatPanel(myMessages, user.uid) { vm.send(t, user, it) }
                        }
                    }
                }
            } else {
                item {
                    Text("Available queues", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                if (queues.isEmpty()) item { EmptyState("🕑", "No queues available yet.\nCheck back soon.") }
                items(queues) { q ->
                    Card(Modifier.fillMaxWidth(), colors = cardColors()) {
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Avatar(q.name.take(1).uppercase())
                            Column(Modifier.weight(1f)) {
                                Text(q.name, style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold)
                                Text("Now serving #${q.currentNumber}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = {
                                    if (needsNotificationPermission()) pendingQueue = q
                                    else vm.join(context, q, user)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandStart, contentColor = Color.White)
                            ) { Text("Join") }
                        }
                    }
                }
            }
            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}


@Composable
fun StaffScreen(user: AppUser, vm: QueueViewModel, onLogout: () -> Unit) {
    val queues by vm.queues.collectAsState()
    val selectedId by vm.selectedQueueId.collectAsState()
    val tickets by vm.staffTickets.collectAsState()
    val message by vm.message.collectAsState()
    val chatId by vm.chatTicketId.collectAsState()
    val chatMessages by vm.chatMessages.collectAsState()
    var newName by remember { mutableStateOf("") }

    val selected = queues.find { it.id == selectedId }
    val waiting = tickets.filter { it.status == "waiting" }
    val called = tickets.filter { it.status == "called" }
    val active = tickets.filter { it.status == "waiting" || it.status == "called" }
        .sortedBy { it.number }
    val chatTicket = active.find { it.id == chatId }

    Column(Modifier.fillMaxSize()) {
        GradientHeader(user.name, if (user.role == "admin") "Admin dashboard" else "Staff dashboard", onLogout)

        LazyColumn(
            Modifier.weight(1f).imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            message?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }

            // Create queue
            item {
                Card(Modifier.fillMaxWidth(), colors = cardColors()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Create a queue", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
                            OutlinedTextField(
                                newName, { newName = it }, label = { Text("Queue name") },
                                singleLine = true, shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { vm.createQueue(newName); newName = "" },
                                enabled = newName.isNotBlank(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandStart, contentColor = Color.White)
                            ) { Text("Add") }
                        }
                    }
                }
            }

            item {
                Text("Your queues", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            }
            if (queues.isEmpty()) item { EmptyState("📋", "No queues yet.\nCreate your first one above.") }
            items(queues) { q ->
                val isSelected = q.id == selectedId
                Card(
                    Modifier.fillMaxWidth().clickable { vm.selectQueue(q.id) },
                    colors = cardColors(),
                    border = if (isSelected) BorderStroke(2.dp, BrandStart) else null
                ) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Avatar(q.name.take(1).uppercase())
                        Column(Modifier.weight(1f)) {
                            Text(q.name, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold)
                            Text(if (isSelected) "Selected" else "Tap to manage",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isSelected) Chip("Managing")
                    }
                }
            }

            if (selected != null) {
                item {
                    Card(Modifier.fillMaxWidth(), colors = cardColors()) {
                        Column(
                            Modifier.fillMaxWidth().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(selected.name, style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold)
                            Text("NOW SERVING", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val current = called.firstOrNull()
                            Text(
                                if (current != null) "#${current.number}" else "—",
                                fontSize = 56.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(current?.userName ?: "Nobody called yet",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Stat("${waiting.size}", "Waiting", Modifier.fillMaxWidth())
                            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { vm.callNext() },
                                    enabled = waiting.isNotEmpty(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandStart, contentColor = Color.White),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) { Text("Call next") }
                                OutlinedButton(
                                    onClick = { vm.noShow() },
                                    enabled = called.isNotEmpty(),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) { Text("No-show") }
                            }
                        }
                    }
                }
                if (chatTicket != null) {
                    item {
                        Card(Modifier.fillMaxWidth(), colors = cardColors()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    Arrangement.SpaceBetween, Alignment.CenterVertically
                                ) {
                                    Text("Chat with ${chatTicket.userName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold)
                                    TextButton(onClick = { vm.openChat(null) }) { Text("Close") }
                                }
                                ChatPanel(chatMessages, user.uid) { vm.send(chatTicket, user, it) }
                            }
                        }
                    }
                }
                item {
                    Text("People in this queue", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                if (active.isEmpty()) item { EmptyState("✅", "Nobody is in this queue.") }
                items(active) { t ->
                    Card(Modifier.fillMaxWidth(), colors = cardColors()) {
                        Row(
                            Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Avatar("#${t.number}")
                            Column(Modifier.weight(1f)) {
                                Text(t.userName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (t.status == "called") "Called" else "Waiting",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = { vm.openChat(t.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Message") }
                        }
                    }
                }
            }
            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}