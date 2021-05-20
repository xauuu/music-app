package com.example.finalproject.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Music
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        albumYear.text = intent.getIntExtra("year", 2021).toString()

//        REST API và đổ dữ liệu vào recyclerview
        val service = ApiAdapter.makeRetrofitService
        val callSong = service.getSongInAlbum(albumId)
        callSong.enqueue(object : Callback<ArrayList<Music>> {
            override fun onResponse(call: Call<ArrayList<Music>>, response: Response<ArrayList<Music>>) {
                songs = response.body()!!
                recyclerView.adapter = SongAdapter(songs, this@AlbumSongsActivity)
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<Music>>, t: Throwable) {
                t.message?.let { Log.e("ERROR LOAD SONG", it) }
            }

        })

//        set on click cho button back
        sectionBackButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun init() {
        progressBar = findViewById(R.id.progressBar)
        val wave = Wave()
        wave.color = resources.getColor(R.color.colorAccent)
        progressBar.indeterminateDrawable = wave
        recyclerView = findViewById(R.id.albumSongsRV)
        albumArt = findViewById(R.id.albumArt)
        albumName = findViewById(R.id.albumName)
        albumYear = findViewById(R.id.albumYear)
        albumSongsDuration = findViewById(R.id.albumSongsDuration)
        sectionBackButton = findViewById(R.id.sectionBackButton)
    }
}