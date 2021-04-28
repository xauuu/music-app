package com.example.finalproject.data

import retrofit2.Response
import retrofit2.http.GET

interface ApiClient {
    @GET("/api/music")
    suspend fun getSongs(): Response<List<Music>>
}