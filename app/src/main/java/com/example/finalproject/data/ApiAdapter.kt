package com.example.finalproject.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiAdapter {
    val makeRetrofitService: ApiClient
        get() {
            return Retrofit.Builder()
                .baseUrl("http://192.168.123.162:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
}