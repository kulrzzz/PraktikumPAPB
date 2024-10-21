package com.tifd.projectcomposed053.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// Retrofit interface to call GitHub API
interface GithubApiService {
    @GET("users/{username}")
    fun getUserProfile(
        @Path("username") username: String,
        @Header("Authorization") authHeader: String
    ): Call<GithubUser>
}
