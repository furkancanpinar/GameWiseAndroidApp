package com.example.gamewise.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamewise.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val displayName: String = "",
    val photoUrl: String? = null,
    val isUploading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.observeUser().collect { user ->
                _uiState.update { current ->

                    val firebaseUrl = user?.photoUrl?.toString()

                    // ✅ FIX: ONLY update from Firebase if we don't already have a newer (timestamped) URL
                    val finalUrl = when {
                        current.photoUrl == null -> firebaseUrl
                        firebaseUrl == null -> current.photoUrl
                        current.photoUrl.startsWith(firebaseUrl) -> current.photoUrl // keep timestamped version
                        else -> current.photoUrl // 🔥 NEVER overwrite local updated image
                    }

                    current.copy(
                        user = user,
                        displayName = user?.displayName ?: current.displayName,
                        photoUrl = finalUrl
                    )
                }
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        _uiState.update { it.copy(isUploading = true, errorMessage = null) }

        viewModelScope.launch {
            val uploadResult = authRepository.uploadProfileImage(uri)

            uploadResult.onSuccess { downloadUrl ->

                val cleanUrl = downloadUrl.toString()
                val timestampedUrl = "$cleanUrl?t=${System.currentTimeMillis()}"

                // 🔥 CRITICAL FIX: update UI FIRST (before Firebase emits anything)
                _uiState.update {
                    it.copy(
                        photoUrl = timestampedUrl,
                        isUploading = false
                    )
                }

                // Update Firebase profile (async, do NOT rely on it for UI)
                authRepository.updateProfile(null, downloadUrl)

            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            authRepository.updateProfile(name, null)
            _uiState.update { it.copy(displayName = name) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}