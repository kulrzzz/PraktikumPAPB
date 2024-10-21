package com.tifd.projectcomposed053.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.tifd.projectcomposed053.api.GithubUser
import com.tifd.projectcomposed053.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

@Composable
fun ProfileScreen(username: String) {
    var userProfile by remember { mutableStateOf<GithubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Token GitHub digunakan untuk autentikasi
    val token = "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"  // Ganti dengan token GitHub Anda
    val authHeader = "token $token"
    Log.d("ProfileScreen", "Token: $token")  // Tambahkan log token

    LaunchedEffect(username) {
        RetrofitClient.githubApiService.getUserProfile(username, authHeader).enqueue(object : Callback<GithubUser> {
            override fun onResponse(call: Call<GithubUser>, response: Response<GithubUser>) {
                if (response.isSuccessful) {
                    userProfile = response.body()
                    isLoading = false
                } else {
                    Log.e("ProfileScreen", "Failed to fetch data: ${response.message()}")
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<GithubUser>, t: Throwable) {
                Log.e("ProfileScreen", "Error fetching profile", t)
                isLoading = false
            }
        })
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
                // Gambar profil berbentuk lingkaran
                Image(
                    painter = rememberAsyncImagePainter(user.avatarUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape) // Membuat gambar bulat
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tampilkan detail profil pengguna
                Text(text = "Username: ${user.username}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = "Name: ${user.name ?: "N/A"}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Followers: ${user.followers}", fontSize = 18.sp)
                Text(text = "Following: ${user.following}", fontSize = 18.sp)
            }
        }
    }
}