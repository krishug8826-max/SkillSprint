package com.krishna.skillsprint.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {


    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email.trim(), password)
                .await()

            val user = result.user
                ?: throw IllegalStateException("Account create nahi hua.")

            val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest
                .Builder()
                .setDisplayName(name.trim())
                .build()

            user.updateProfile(profileUpdate).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email.trim(), password)
                .await()

            val user = result.user
                ?: throw IllegalStateException("Login nahi hua.")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }


}
