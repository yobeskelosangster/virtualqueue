
package com.example.virtualqueue

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class QueueRepository {

    private val db = FirebaseFirestore.getInstance()

    fun queuesFlow(): Flow<List<QueueInfo>> = callbackFlow {
        val reg = db.collection("queues").addSnapshotListener { snap, _ ->
            trySend(snap?.toObjects(QueueInfo::class.java) ?: emptyList())
        }
        awaitClose { reg.remove() }
    }

    private fun ticketsFlow(query: Query): Flow<List<Ticket>> = callbackFlow {
        val reg = query.addSnapshotListener { snap, _ ->
            val list = snap?.toObjects(Ticket::class.java) ?: emptyList()
            trySend(list.sortedBy { it.number })
        }
        awaitClose { reg.remove() }
    }

    fun myTicketsFlow(uid: String) =
        ticketsFlow(db.collection("tickets").whereEqualTo("userId", uid))

    fun queueTicketsFlow(queueId: String) =
        ticketsFlow(db.collection("tickets").whereEqualTo("queueId", queueId))

    suspend fun createQueue(name: String) {
        db.collection("queues").add(
            QueueInfo(
                name = name,
                status = "open",
                currentNumber = 0,
                lastIssued = 0,
                avgServiceMinutes = 5
            )
        ).await()
    }

    suspend fun joinQueue(queue: QueueInfo, user: AppUser) {
        val qRef = db.collection("queues").document(queue.id)
        val tRef = db.collection("tickets").document()

        db.runTransaction { tx ->
            val snap = tx.get(qRef)
            val next = (snap.getLong("lastIssued") ?: 0L) + 1

            tx.update(qRef, "lastIssued", next)
            tx.set(
                tRef,
                Ticket(
                    queueId = queue.id,
                    queueName = queue.name,
                    number = next,
                    userId = user.uid,
                    userName = user.name,
                    status = "waiting",
                    joinedAt = System.currentTimeMillis()
                )
            )

            next
        }.await()
    }

    suspend fun cancelTicket(ticketId: String) {
        db.collection("tickets")
            .document(ticketId)
            .update("status", "cancelled")
            .await()
    }

    suspend fun callNext(queueId: String, tickets: List<Ticket>) {
        val next = tickets
            .filter { it.status == "waiting" }
            .minByOrNull { it.number }

        val batch = db.batch()
        val ticketCollection = db.collection("tickets")

        tickets.filter { it.status == "called" }.forEach { ticket ->
            batch.update(
                ticketCollection.document(ticket.id),
                "status",
                "served"
            )
        }

        if (next != null) {
            batch.update(
                ticketCollection.document(next.id),
                "status",
                "called"
            )
            batch.update(
                ticketCollection.document(next.id),
                "calledAt",
                System.currentTimeMillis()
            )
            batch.update(
                db.collection("queues").document(queueId),
                "currentNumber",
                next.number
            )
        }

        batch.commit().await()
    }
    suspend fun markNoShow(queueId: String, tickets: List<Ticket>) {
        val calledTickets = tickets.filter { it.status == "called" }

        if (calledTickets.isEmpty()) {
            return
        }

        val next = tickets
            .filter { it.status == "waiting" }
            .minByOrNull { it.number }

        val batch = db.batch()
        val ticketCollection = db.collection("tickets")

        calledTickets.forEach { ticket ->
            batch.update(
                ticketCollection.document(ticket.id),
                "status",
                "skipped"
            )
        }

        if (next != null) {
            batch.update(
                ticketCollection.document(next.id),
                "status",
                "called"
            )
            batch.update(
                ticketCollection.document(next.id),
                "calledAt",
                System.currentTimeMillis()
            )
            batch.update(
                db.collection("queues").document(queueId),
                "currentNumber",
                next.number
            )
        }

        batch.commit().await()
    }

    fun messagesFlow(ticketId: String): Flow<List<Message>> = callbackFlow {
        val reg = db.collection("messages")
            .whereEqualTo("ticketId", ticketId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.toObjects(Message::class.java) ?: emptyList()
                trySend(list.sortedBy { it.sentAt })
            }

        awaitClose { reg.remove() }
    }

    suspend fun sendMessage(ticket: Ticket, from: AppUser, text: String) {
        db.collection("messages").add(
            Message(
                ticketId = ticket.id,
                queueId = ticket.queueId,
                fromUserId = from.uid,
                fromName = from.name,
                fromStaff = from.role == "staff" || from.role == "admin",
                text = text,
                sentAt = System.currentTimeMillis()
            )
        ).await()
    }
}
