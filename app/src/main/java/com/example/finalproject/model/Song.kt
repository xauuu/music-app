package com.example.finalproject.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
        @SerializedName("favorite_id") val idFavorite: Int,
        val id: Int,
        val name: String?,
        val album: String?,
        val artist: String?,
        val imageUrl: String?,
        val songUrl: String?,
) : Parcelable
