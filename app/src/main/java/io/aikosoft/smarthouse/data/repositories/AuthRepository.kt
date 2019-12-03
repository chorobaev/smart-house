package io.aikosoft.smarthouse.data.repositories

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val isLoggedIn: Boolean get() = firebaseAuth.currentUser != null

    fun logout() = firebaseAuth.signOut()
}