package com.example.finalproject.api

import com.example.finalproject.model.Album
import com.example.finalproject.model.CheckUser
import com.example.finalproject.model.Song
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// Định nghĩa các phương thức gửi lên sv
interface ApiClient {

//    Đăng nhâp
    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<CheckUser>

//    Lấy bảng xếp hạng bài hát
    @GET("/api/music")
    suspend fun getSongs(): Response<ArrayList<Song>>

//    Lấy tất cả album
    @GET("/api/album")
    suspend fun getAlbums(): Response<ArrayList<Album>>

//    Lấy tất cả bài hát thuộc album
    @GET("/api/album/{id}")
    suspend fun getSongInAlbum(@Path("id") id: Int): Response<ArrayList<Song>>

//    Cập nhật lượt nghe bài hát
    @PUT("/api/update-view/{id}")
    suspend fun updateSong(@Path("id") id: Int)

    @GET("api/search/{keyword}")
    fun search(@Path("keyword") keyword: String): Call<ArrayList<Song>>

    @GET("api/favorite")
    suspend fun allFavorite(): Response<ArrayList<Song>>

    @POST("api/favorite")
    suspend fun addFavorite(): Response<String>

    @DELETE("api/favorite")
    suspend fun deleteFavorite(): Response<String>
}