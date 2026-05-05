package com.example.gamewise.data.auth

import android.net.Uri
import android.util.Log
import com.example.gamewise.network.EmailRequest
import com.example.gamewise.network.EmailService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import com.google.firebase.Timestamp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val retrofit = Retrofit.Builder()
        .baseUrl(EmailService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val emailService = retrofit.create(EmailService::class.java)

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun observeUser(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun updateProfile(name: String?, photoUri: Uri?): Result<Unit> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
            name?.let { profileUpdates.displayName = it }
            photoUri?.let { profileUpdates.photoUri = it }
            user.updateProfile(profileUpdates.build()).await()
            user.reload().await() // Refresh the local user object to reflect changes
            
            // Also save to Firestore
            val userData = mutableMapOf<String, Any>()
            name?.let { userData["displayName"] = it }
            photoUri?.let { userData["photoUrl"] = it.toString() }
            
            if (userData.isNotEmpty()) {
                firestore.collection("users").document(user.uid)
                    .set(userData, SetOptions.merge())
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Profile update failed", e)
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(uri: Uri): Result<Uri> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))
        return try {
            Log.d("AuthRepository", "Starting upload for user: ${user.uid}")
            // Using a directory-based path makes security rules much easier to manage
            val storageRef = firebaseStorage.reference.child("avatars/${user.uid}/profile.jpg")
            
            // Add metadata to specify content type
            val metadata = storageMetadata {
                contentType = "image/jpeg"
            }
            
            storageRef.putFile(uri, metadata).await()
            val downloadUrl = storageRef.downloadUrl.await()
            Log.d("AuthRepository", "Upload successful: $downloadUrl")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Storage upload error", e)
            Result.failure(e)
        }
    }

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
            
            // Create initial user document in Firestore
            val userData = mapOf(
                "email" to email,
                "uid" to user.uid,
                "createdAt" to Timestamp.now()
            )
            firestore.collection("users").document(user.uid).set(userData).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendOtpEmail(email: String, otp: String): Boolean {
        // template_params must match the placeholders in your EmailJS template.
        // For example, if your template uses {{otp_code}} and {{to_email}}, 
        // the map below will fill those values.
        val params = mapOf(
            "to_email" to email,
            "verification_code" to otp,
            "from_name" to "GameWise"
        )
        val request = EmailRequest(
            serviceId = EmailService.SERVICE_ID,
            templateId = EmailService.TEMPLATE_ID,
            userId = EmailService.PUBLIC_KEY,
            accessToken = EmailService.PRIVATE_KEY,
            templateParams = params
        )
        return try {
            val response = emailService.sendVerificationEmail(request)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("AuthRepository", "EmailJS Error: ${response.code()} - $errorBody")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AuthRepository", "EmailJS Exception", e)
            false
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
