package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.data.ApiAdapter
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
    lateinit var albumSongsDuration: TextView
    lateinit var songs: ArrayList<Music>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_songs)

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.albumSongsRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

        albumArt =findViewById(R.id.albumArt)
        albumName = findViewById(R.id.albumName)
        albumSongsDuration = findViewById(R.id.albumSongsDuration)

            albumId = intent.getIntExtra("id", -1)
            Glide.with(this).load(intent.getStringExtra("image")).into(albumArt)
            albumName.text = intent.getStringExtra("name")

        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            val response = service.getSongInAlbum(albumId)
            withContext(context = Dispatchers.Main) {
                if (response.isSuccessful) {
                    songs = response.body()!!
                    recyclerView.adapter = SongAdapter(songs,
                        this@AlbumSongsActivity
                    )
                    albumSongsDuration.text = "${songs.size.toString()} Bài hát"
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
}