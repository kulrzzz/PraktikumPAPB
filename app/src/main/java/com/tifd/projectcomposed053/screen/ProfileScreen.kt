package com.tifd.projectcomposed053.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.tifd.projectcomposed053.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(username: String, profileViewModel: ProfileViewModel = viewModel()) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    // Memuat data profil pengguna saat pertama kali ditampilkan
    LaunchedEffect(username) {
        profileViewModel.fetchUserProfile(username)
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        userProfile?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Menampilkan gambar profil berbentuk lingkaran
                Image(
                    painter = rememberAsyncImagePainter(user.avatarUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Menampilkan informasi pengguna
                Text(text = "Username: ${user.username}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = "Name: ${user.name ?: "N/A"}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Followers: ${user.followers}", fontSize = 18.sp)
                Text(text = "Following: ${user.following}", fontSize = 18.sp)
            }
        } ?: run {
            Text("Profile not found", color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
        }
    }
}