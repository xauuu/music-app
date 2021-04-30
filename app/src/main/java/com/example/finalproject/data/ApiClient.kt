package com.example.finalproject.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiClient {
    @GET("/api/music")
    suspend fun getSongs(): Response<ArrayList<Music>>

    @GET("/api/album")
    suspend fun getAlbums(): Response<ArrayList<Album>>

    @GET("/api/album/{id}")
    suspend fun getSongInAlbum(@Path("id") id: Int): Response<ArrayList<Music>>
}