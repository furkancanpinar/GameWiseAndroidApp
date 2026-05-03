package com.example.gamewise.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// EmailJS Request Data Model
data class EmailRequest(
    @SerializedName("service_id") val serviceId: String,
    @SerializedName("template_id") val templateId: String,
    @SerializedName("user_id") val userId: String, // EmailJS Public Key
    @SerializedName("accessToken") val accessToken: String? = null, // EmailJS Private Key
    @SerializedName("template_params") val templateParams: Map<String, String>
)

interface EmailService {
    @POST("api/v1.0/email/send")
    suspend fun sendVerificationEmail(@Body request: EmailRequest): Response<Void>

    companion object {
        const val BASE_URL = "https://api.emailjs.com/"
        const val SERVICE_ID = "service_c1g00tp"
        const val TEMPLATE_ID = "template_h33h85b"
        const val PUBLIC_KEY = "g5BLTUe0sqV20jyvn"
        const val PRIVATE_KEY = "XXl_Vsz3uvgp5fXzn30xN"
    }
}
