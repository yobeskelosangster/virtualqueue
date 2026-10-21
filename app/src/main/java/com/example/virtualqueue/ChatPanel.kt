package com.example.virtualqueue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatPanel(messages: List<Message>, myUid: String, onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (messages.isEmpty()) {
            Text("No messages yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        messages.takeLast(30).forEach { m ->
            val mine = m.fromUserId == myUid
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (mine) BrandStart else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        if (!mine) {
                            Text(
                                m.fromName + if (m.fromStaff) " (staff)" else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            m.text,
                            color = if (mine) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                text, { text = it }, label = { Text("Type a message") },
                shape = RoundedCornerShape(14.dp), modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { onSend(text); text = "" },
                enabled = text.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandStart, contentColor = Color.White)
            ) { Text("Send") }
        }
    }
}