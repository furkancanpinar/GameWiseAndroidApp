package com.example.gamewise.data.auth

import com.example.gamewise.network.EmailRequest
import com.example.gamewise.network.EmailService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // --- EMAILJS CONFIGURATION ---
    // Place your EmailJS credentials here
    private val EMAILJS_SERVICE_ID = "YOUR_SERVICE_ID"
    private val EMAILJS_TEMPLATE_ID = "YOUR_TEMPLATE_ID"
    private val EMAILJS_PUBLIC_KEY = "YOUR_PUBLIC_KEY"
    // -----------------------------

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.emailjs.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val emailService = retrofit.create(EmailService::class.java)

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user!!
            
            // Send Verification Email via EmailJS
            sendVerificationEmail(email)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun sendVerificationEmail(email: String) {
        val params = mapOf(
            "to_email" to email,
            "message" to "Welcome to GameWise! Please verify your account by clicking the link in your dashboard."
        )
        val request = EmailRequest(
            service_id = EMAILJS_SERVICE_ID,
            template_id = EMAILJS_TEMPLATE_ID,
            user_id = EMAILJS_PUBLIC_KEY,
            template_params = params
        )
        try {
            emailService.sendVerificationEmail(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
