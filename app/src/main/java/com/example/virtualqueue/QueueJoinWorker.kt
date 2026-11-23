
package com.example.virtualqueue

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QueueJoinWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {

        // Retrieve the pending request information
        val requestId = inputData.getString(KEY_REQUEST_ID)
            ?: return Result.failure()

        val queueId = inputData.getString(KEY_QUEUE_ID)
            ?: return Result.failure()

        val queueName = inputData.getString(KEY_QUEUE_NAME)
            ?: return Result.failure()

        val userId = inputData.getString(KEY_USER_ID)
            ?: return Result.failure()

        val userName = inputData.getString(KEY_USER_NAME)
            ?: return Result.failure()

        val joinedAt = inputData.getLong(KEY_JOINED_AT, 0L)

        return try {

            val queueRef = db.collection("queues")
                .document(queueId)

            // Use the request ID as the ticket document ID.
            val ticketRef = db.collection("tickets")
                .document(requestId)

            val ticketNumber = db.runTransaction { transaction ->

                // Check whether this request was already processed.
                val existingTicket = transaction.get(ticketRef)

                if (existingTicket.exists()) {
                    return@runTransaction existingTicket
                        .getLong("number") ?: 0L
                }

                // Read the queue before making any changes.
                val queueSnapshot = transaction.get(queueRef)

                if (!queueSnapshot.exists()) {
                    throw IllegalStateException("Queue does not exist")
                }

                val nextNumber =
                    (queueSnapshot.getLong("lastIssued") ?: 0L) + 1L

                // Update the queue counter.
                transaction.update(
                    queueRef,
                    "lastIssued",
                    nextNumber
                )

                // Create the student's ticket.
                val ticketData = hashMapOf(
                    "queueId" to queueId,
                    "queueName" to queueName,
                    "number" to nextNumber,
                    "userId" to userId,
                    "userName" to userName,
                    "status" to "waiting",
                    "joinedAt" to joinedAt
                )

                transaction.set(ticketRef, ticketData)

                nextNumber

            }.await()

            if (ticketNumber <= 0L) {
                Result.failure()
            } else {
                Result.success()
            }

        } catch (e: Exception) {

            // Ask WorkManager to retry the operation.
            Result.retry()
        }
    }

    companion object {
        const val KEY_REQUEST_ID = "request_id"
        const val KEY_QUEUE_ID = "queue_id"
        const val KEY_QUEUE_NAME = "queue_name"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_JOINED_AT = "joined_at"
    }
}
