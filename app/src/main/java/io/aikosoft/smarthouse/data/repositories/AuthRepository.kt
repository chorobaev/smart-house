package io.aikosoft.smarthouse.data.repositories

import com.google.firebase.auth.FirebaseAuth
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val isLoggedIn: Boolean get() = firebaseAuth.currentUser != null
    val uid: String get() = firebaseAuth.uid ?: throw IllegalStateException("Not signed in")

    fun logout() = firebaseAuth.signOut()
}