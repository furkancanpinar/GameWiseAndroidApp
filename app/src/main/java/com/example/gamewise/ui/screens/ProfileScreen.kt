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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamewise.ui.viewmodels.ProfileViewModel
import com.example.gamewise.ui.theme.GameWisePurple
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // Correctly observe StateFlow with lifecycle awareness
    val uiState by viewModel.uiState.collectAsState()

    var editingName by remember { mutableStateOf("") }
    var isEditingName by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
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
                // AsyncImage reloads instantly because uiState.photoUrl changes with timestamp
                AsyncImage(
                    model = uiState.photoUrl,
                    contentDescription = "Profile Picture",
                    placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_report_image),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            if (uiState.isUploading) {
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
                    value = editingName,
                    onValueChange = { editingName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.updateDisplayName(editingName)
                            isEditingName = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save Name", tint = Color.Green)
                        }
                    }
                )
            } else {
                Text(
                    text = if (uiState.displayName.isNotBlank()) uiState.displayName else "Set Username",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameWisePurple
                )
                IconButton(onClick = {
                    editingName = uiState.displayName
                    isEditingName = true
                }) {
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
                    Text(uiState.user?.email ?: "Not available", style = MaterialTheme.typography.bodyLarge)
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
                    Text(uiState.user?.uid ?: "Not available", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Sign out logic should probably move to ViewModel too
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
