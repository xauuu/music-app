package com.example.finalproject.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.finalproject.data.Music

class MusicService: Service() {

    var mIBinder: IBinder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null
    var musicList: ArrayList<Music> = ArrayList()
    lateinit var uri: Uri
    private var postion = -1

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e("bind", "method")
        return mIBinder
    }

    class MusicBinder : Binder() {
        val service: MusicService
            get() = MusicService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

}