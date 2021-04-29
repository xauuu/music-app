package com.example.finalproject.data

import android.os.Parcel
import android.os.Parcelable

data class Music(
        val id: Int,
        val name: String?,
        val album: String?,
        val singer: String?,
        val imageUrl: String?,
        val songUrl: String?
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(id)
                parcel.writeString(name)
                parcel.writeString(album)
                parcel.writeString(singer)
                parcel.writeString(imageUrl)
                parcel.writeString(songUrl)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Music> {
                override fun createFromParcel(parcel: Parcel): Music {
                        return Music(parcel)
                }

                override fun newArray(size: Int): Array<Music?> {
                        return arrayOfNulls(size)
                }
        }
}
