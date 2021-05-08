package com.example.finalproject.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Khởi tạo Retrofit
object ApiAdapter {
    private const val BASE_URL = "http://172.20.10.5:8000"
    val makeRetrofitService: ApiClient
        get() {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
}


