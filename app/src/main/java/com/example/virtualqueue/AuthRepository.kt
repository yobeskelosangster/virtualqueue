package com.example.virtualqueue

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val STAFF_CODE = "QUEUE2026"   // change this to your own secret
    }

    suspend fun register(name: String, email: String, password: String, staffCode: String): Result<AppUser> = try {
        if (staffCode.isNotBlank() && staffCode != STAFF_CODE) {
            throw Exception("Wrong staff access code")
        }
        val role = if (staffCode.isNotBlank()) "staff" else "customer"
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user!!.uid
        val user = AppUser(uid, name, email, role)
        db.collection("users").document(uid).set(user).await()
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun login(email: String, password: String): Result<AppUser> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(fetchUser(result.user!!.uid))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun fetchUser(uid: String): AppUser =
        db.collection("users").document(uid).get().await().toObject(AppUser::class.java)
            ?: AppUser(uid = uid)

    suspend fun currentUserOrNull(): AppUser? {
        val firebaseUser = auth.currentUser ?: return null
        return try { fetchUser(firebaseUser.uid) } catch (e: Exception) { null }
    }

    fun logout() = auth.signOut()
}
