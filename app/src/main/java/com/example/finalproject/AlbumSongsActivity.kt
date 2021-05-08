package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.data.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumSongsActivity : AppCompatActivity() {
    private var albumId = -1
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var albumArt: ImageView
    lateinit var albumName: TextView
    lateinit var albumYear: TextView
    lateinit var sectionBackButton: Button
    lateinit var albumSongsDuration: TextView
    lateinit var songs: ArrayList<Music>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_songs)

        init()

        recyclerView.layoutManager = LinearLayoutManager(this)

//        Lấy dữ liệu từ intent
        albumId = intent.getIntExtra("id", -1)
        Glide.with(this).load(intent.getStringExtra("image")).into(albumArt)
        albumName.text = intent.getStringExtra("name")
        albumYear.text = intent.getIntExtra("Year", 2021).toString()

//        REST API và đổ dữ liệu vào recyclerview
        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            try {
                val response = service.getSongInAlbum(albumId)
                withContext(context = Dispatchers.Main) {
                    if (response.isSuccessful) {
                        songs = response.body()!!
                        recyclerView.adapter = SongAdapter(
                            data = songs, context = this@AlbumSongsActivity,
                        )
                        albumSongsDuration.text = songs.size.toString() + " Bài hát"
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }
        }

//        set on click cho button back
        sectionBackButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun init() {
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.albumSongsRV)
        albumArt = findViewById(R.id.albumArt)
        albumName = findViewById(R.id.albumName)
        albumYear = findViewById(R.id.albumYear)
        albumSongsDuration = findViewById(R.id.albumSongsDuration)
        sectionBackButton = findViewById(R.id.sectionBackButton)
    }
}