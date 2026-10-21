package com.example.virtualqueue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val user: AppUser? = null,
    val loading: Boolean = true,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        viewModelScope.launch {
            _state.value = AuthState(user = repo.currentUserOrNull(), loading = false)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        repo.login(email.trim(), password)
            .onSuccess { _state.value = AuthState(user = it, loading = false) }
            .onFailure { _state.value = AuthState(loading = false, error = it.message) }
    }

    fun register(name: String, email: String, password: String, staffCode: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        repo.register(name.trim(), email.trim(), password, staffCode.trim())
            .onSuccess { _state.value = AuthState(user = it, loading = false) }
            .onFailure { _state.value = AuthState(loading = false, error = it.message) }
    }
    fun logout() {
        repo.logout()
        _state.value = AuthState(loading = false)
    }
}