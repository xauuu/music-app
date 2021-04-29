package com.example.finalproject.data

import retrofit2.Response
import retrofit2.http.GET

interface ApiClient {
    @GET("/api/music")
    suspend fun getSongs(): Response<ArrayList<Music>>

    @GET("/api/album")
    suspend fun getAlbums(): Response<ArrayList<Album>>
}