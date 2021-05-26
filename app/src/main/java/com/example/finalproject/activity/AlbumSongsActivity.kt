package com.example.finalproject.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Song
import com.github.ybq.android.spinkit.style.FadingCircle
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
    lateinit var amount: TextView
    lateinit var songs: ArrayList<Song>

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
        callSong.enqueue(object : Callback<ArrayList<Song>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ArrayList<Song>>, response: Response<ArrayList<Song>>) {
                songs = response.body()!!
                amount.text = "${songs.size} Bài hát"
                recyclerView.adapter = SongAdapter(songs, this@AlbumSongsActivity)
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<Song>>, t: Throwable) {
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
        val wave = FadingCircle()
        wave.color = resources.getColor(R.color.colorAccent)
        progressBar.indeterminateDrawable = wave
        recyclerView = findViewById(R.id.albumSongsRV)
        albumArt = findViewById(R.id.albumArt)
        albumName = findViewById(R.id.albumName)
        albumYear = findViewById(R.id.albumYear)
        amount = findViewById(R.id.amount)
        sectionBackButton = findViewById(R.id.sectionBackButton)
    }
}