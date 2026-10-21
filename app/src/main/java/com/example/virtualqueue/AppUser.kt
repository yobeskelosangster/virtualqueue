package com.example.virtualqueue

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "customer"   // "customer", "staff" or "admin"
)