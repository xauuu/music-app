package com.example.finalproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(
        val id: Int,
        val name: String?,
        val album: String?,
        val artist: String?,
        val imageUrl: String?,
        val songUrl: String?
) : Parcelable
