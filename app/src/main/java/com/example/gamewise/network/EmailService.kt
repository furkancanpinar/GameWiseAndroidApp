package com.example.gamewise.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// EmailJS Request Data Model
data class EmailRequest(
    val service_id: String,
    val template_id: String,
    val user_id: String, // This is your EmailJS Public Key
    val template_params: Map<String, String>
)

interface EmailService {
    @POST("api/v1.0/email/send")
    suspend fun sendVerificationEmail(@Body request: EmailRequest): Response<Unit>
}
