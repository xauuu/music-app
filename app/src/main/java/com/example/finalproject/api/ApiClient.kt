package com.example.finalproject.api

import com.example.finalproject.model.*
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

    @GET("api/favorite/{id}")
    suspend fun allFavorite(@Path("id") id: Int): Response<ArrayList<Song>>

    @FormUrlEncoded
    @POST("api/favorite")
    suspend fun addFavorite(@Field("user_id") userId: Int, @Field("song_id") songId: Int): Response<Result>

    @DELETE("api/favorite/{id}")
    suspend fun deleteFavorite(@Path("id") id: Int): Response<Result>

    @GET("api/history/{id}")
    suspend fun allHistory(@Path("id") id: Int, @Query("limit") limit: Int?): Response<ArrayList<Song>>

    @FormUrlEncoded
    @POST("api/history")
    suspend fun addHistory(@Field("user_id") userId: Int, @Field("song_id") songId: Int): Response<Result>

    @DELETE("api/history/{id}")
    suspend fun deleteHistory(@Path("id") id: Int): Response<Result>
}