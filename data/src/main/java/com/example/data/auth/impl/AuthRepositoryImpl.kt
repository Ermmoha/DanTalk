package com.example.data.auth.impl

import com.example.core.auth.exception.SignInException
import com.example.core.auth.exception.SignUpException
import com.example.data.auth.api.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun createUser(email: String, password: String) : String =
        suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = firebaseAuth.currentUser
                    if (user != null) continuation.resume(user.uid)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(
                        when (it) {
                            is FirebaseAuthWeakPasswordException -> SignUpException.WeakPasswordException()
                            is FirebaseNetworkException -> SignUpException.NetworkException()
                            else -> it
                        }

                    )
                }
        }

    override suspend fun login(email: String, password: String): String =
        suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { task ->
                    val user = task.user
                    if (user != null) continuation.resume(user.uid)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(
                        when (it) {
                            is FirebaseAuthInvalidCredentialsException -> SignInException.InvalidCredentials()
                            is FirebaseAuthEmailException -> SignInException.InvalidEmailFormat()
                            is FirebaseNetworkException -> SignInException.NetworkError()
                            else -> it
                        }
                    )
                }
        }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}