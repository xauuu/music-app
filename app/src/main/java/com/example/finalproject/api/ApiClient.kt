package com.example.finalproject.api

import com.example.finalproject.model.Album
import com.example.finalproject.model.CheckUser
import com.example.finalproject.model.Music
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// Định nghĩa các phương thức gửi lên sv
interface ApiClient {

//    Đăng nhâp
    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<CheckUser>

//    Lấy tất cả bài hát
    @GET("/api/music")
    fun getSongs(): Call<ArrayList<Music>>

//    Lấy tất cả album
    @GET("/api/album")
    fun getAlbums(): Call<ArrayList<Album>>

//    Lấy tất cả bài hát thuộc album
    @GET("/api/album/{id}")
    fun getSongInAlbum(@Path("id") id: Int): Call<ArrayList<Music>>

//    Cập nhật lượt nghe bài hát
    @PUT("/api/update-view/{id}")
    fun updateSong(@Path("id") id: Int): Call<Void>

    @GET("api/search/{keyword}")
    fun search(@Path("keyword") keyword: String): Call<ArrayList<Music>>

}