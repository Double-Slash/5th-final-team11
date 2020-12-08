package com.doubleslas.fifith.alcohol.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.doubleslas.fifith.alcohol.model.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {
    private val authRepository by lazy { AuthRepository() }
    private var firebaseAuth = FirebaseAuth.getInstance()

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser?.displayName
                    authRepository.completeLogin(credential)
                    Log.d("junmin", user.toString())
                }
            }
    }

}