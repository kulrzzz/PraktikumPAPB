package com.tifd.projectcomposed053.viewmodel

import androidx.lifecycle.ViewModel
import com.tifd.projectcomposed053.BuildConfig
import com.tifd.projectcomposed053.api.GithubUser
import com.tifd.projectcomposed053.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<GithubUser?>(null)
    val userProfile: StateFlow<GithubUser?> get() = _userProfile

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Memuat profil pengguna dari GitHub
    fun fetchUserProfile(username: String) {
        val token = "Bearer ${BuildConfig.API_KEY}" // Mengambil token dari BuildConfig
        _isLoading.value = true

        val call = RetrofitClient.githubApiService.getUserProfile(username, token)

        // Menangani panggilan Retrofit secara asinkron dengan enqueue
        call.enqueue(object : Callback<GithubUser> {
            override fun onResponse(call: Call<GithubUser>, response: Response<GithubUser>) {
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                } else {
                    // Tangani jika ada kesalahan HTTP
                    _userProfile.value = null
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<GithubUser>, t: Throwable) {
                // Tangani jika ada kegagalan jaringan atau kesalahan lainnya
                _userProfile.value = null
                _isLoading.value = false
            }
        })
    }
}