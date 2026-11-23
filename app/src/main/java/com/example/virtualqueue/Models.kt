package com.example.virtualqueue

import com.google.firebase.firestore.DocumentId

data class QueueInfo(
    @DocumentId val id: String = "",
    val name: String = "",
    val status: String = "open",
    val currentNumber: Long = 0,
    val lastIssued: Long = 0,
    val avgServiceMinutes: Long = 5
)


data class Ticket(
    @DocumentId val id: String = "",
    val queueId: String = "",
    val queueName: String = "",
    val number: Long = 0,
    val userId: String = "",
    val userName: String = "",
    val status: String = "waiting", // waiting, called, served, skipped, cancelled
    val joinedAt: Long = 0,
    val calledAt: Long = 0
)


data class Message(
    @DocumentId val id: String = "",
    val ticketId: String = "",
    val queueId: String = "",
    val fromUserId: String = "",
    val fromName: String = "",
    val fromStaff: Boolean = false,
    val text: String = "",
    val sentAt: Long = 0
)