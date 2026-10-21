package com.example.virtualqueue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class QueueViewModel : ViewModel() {
    private val repo = QueueRepository()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val share = SharingStarted.WhileSubscribed(5000)

    val message = MutableStateFlow<String?>(null)
    val selectedQueueId = MutableStateFlow<String?>(null)
    val chatTicketId = MutableStateFlow<String?>(null)

    val queues = repo.queuesFlow().stateIn(viewModelScope, share, emptyList())

    // Customer side
    val myTicket = repo.myTicketsFlow(uid)
        .map { list ->
            list.filter { it.status == "waiting" || it.status == "called" }
                .maxByOrNull { it.joinedAt }
        }
        .stateIn(viewModelScope, share, null)

    val peopleAhead = myTicket
        .flatMapLatest { t ->
            if (t == null) flowOf(0)
            else repo.queueTicketsFlow(t.queueId).map { list ->
                list.count { it.status == "waiting" && it.number < t.number }
            }
        }
        .stateIn(viewModelScope, share, 0)

    val myMessages = myTicket
        .map { it?.id }
        .distinctUntilChanged()
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repo.messagesFlow(id)
        }
        .stateIn(viewModelScope, share, emptyList())

    // Staff side
    val staffTickets = selectedQueueId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repo.queueTicketsFlow(id)
        }
        .stateIn(viewModelScope, share, emptyList())

    val chatMessages = chatTicketId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repo.messagesFlow(id)
        }
        .stateIn(viewModelScope, share, emptyList())

    private fun run(block: suspend () -> Unit) {
        viewModelScope.launch {
            try { block() } catch (e: Exception) { message.value = e.message }
        }
    }

    fun selectQueue(id: String) { selectedQueueId.value = id; chatTicketId.value = null }
    fun openChat(ticketId: String?) { chatTicketId.value = ticketId }
    fun clearMessage() { message.value = null }
    fun createQueue(name: String) = run { repo.createQueue(name.trim()) }
    fun join(queue: QueueInfo, user: AppUser) = run { repo.joinQueue(queue, user) }
    fun cancel(ticketId: String) = run { repo.cancelTicket(ticketId) }
    fun send(ticket: Ticket, user: AppUser, text: String) =
        run { repo.sendMessage(ticket, user, text.trim()) }

    fun callNext() = run {
        val id = selectedQueueId.value ?: return@run
        repo.callNext(id, staffTickets.value)
    }

    fun noShow() = run { repo.markNoShow(staffTickets.value) }
}