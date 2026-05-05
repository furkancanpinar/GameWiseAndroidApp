package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gamewise.data.auth.AuthRepository
import com.example.gamewise.ui.theme.GameWisePurple
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    val user by authRepository.observeUser().collectAsState(initial = authRepository.getCurrentUser())
    val scope = rememberCoroutineScope()

    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var photoUri by remember { mutableStateOf<Uri?>(user?.photoUrl) }
    var isUploading by remember { mutableStateOf(false) }
    var isEditingName by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Sync state with user data when it changes externally
    LaunchedEffect(user) {
        if (!isEditingName) {
            displayName = user?.displayName ?: ""
        }
        if (!isUploading) {
            photoUri = user?.photoUrl
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch {
                val uploadResult = authRepository.uploadProfileImage(it)
                if (uploadResult.isSuccess) {
                    val downloadUrl = uploadResult.getOrThrow()
                    val updateResult = authRepository.updateProfile(null, downloadUrl)

                    if (updateResult.isSuccess) {
                        // Add a timestamp to the URI to force Coil to reload the image from the network
                        val bustedUri = Uri.parse(downloadUrl.toString() + "&t=${System.currentTimeMillis()}")
                        photoUri = bustedUri
                    } else {
                        errorMessage = "Profile update failed: ${updateResult.exceptionOrNull()?.message}"
                    }
                } else {
                    errorMessage = "Upload failed: ${uploadResult.exceptionOrNull()?.message}"
                }
                isUploading = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // If you want to use the parent's background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.BottomEnd
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    tint = GameWisePurple
                )
            }

            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.matchParentSize(),
                    color = GameWisePurple
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = GameWisePurple,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Picture",
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isEditingName) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                authRepository.updateProfile(displayName, null)
                                isEditingName = false
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save Name", tint = Color.Green)
                        }
                    }
                )
            } else {
                Text(
                    text = if (displayName.isNotBlank()) displayName else "Set Username",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameWisePurple
                )
                IconButton(onClick = { isEditingName = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Name", modifier = Modifier.size(20.dp), tint = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Email Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Email, contentDescription = null, tint = GameWisePurple)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Email Address", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(user?.email ?: "Not available", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User ID Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GameWisePurple)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("User ID", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(user?.uid ?: "Not available", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                authRepository.logout()
                onSignOut()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
}
