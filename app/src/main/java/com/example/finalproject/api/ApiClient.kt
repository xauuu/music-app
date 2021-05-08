package com.example.finalproject.api

import com.example.finalproject.data.Album
import com.example.finalproject.data.Music
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

// Định nghĩa các phương thức gửi lên sv
interface ApiClient {

//    Lấy tất cả bài hát
    @GET("/api/music")
    suspend fun getSongs(): Response<ArrayList<Music>>

//    Lấy tất cả album
    @GET("/api/album")
    suspend fun getAlbums(): Response<ArrayList<Album>>

//    Lấy tất cả bài hát thuộc album
    @GET("/api/album/{id}")
    suspend fun getSongInAlbum(@Path("id") id: Int): Response<ArrayList<Music>>

//    Cập nhật lượt nghe bài hát
    @PUT("/api/update-view/{id}")
    suspend fun updateSong(@Path("id") id: Int)

}