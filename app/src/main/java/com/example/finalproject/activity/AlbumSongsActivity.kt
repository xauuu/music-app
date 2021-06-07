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
    lateinit var amount: TextView
    lateinit var songs: ArrayList<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_songs)

        progressBar = findViewById(R.id.progressBar)

        albumArt = findViewById(R.id.albumArt)
        albumName = findViewById(R.id.albumName)
        albumYear = findViewById(R.id.albumYear)
        amount = findViewById(R.id.amount)
        sectionBackButton = findViewById(R.id.sectionBackButton)

        recyclerView = findViewById(R.id.albumSongsRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        Lấy dữ liệu từ intent
        albumId = intent.getIntExtra("id", -1)

        Glide.with(this).load(intent.getStringExtra("image")).into(albumArt)
        albumName.text = intent.getStringExtra("name")
        albumYear.text = intent.getIntExtra("year", 2021).toString()

        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getSongInAlbum(albumId)
                withContext(context = Dispatchers.Main) {
                    songs = response.body()!!
                    amount.text = "${songs.size} Bài hát"
                    recyclerView.adapter = SongAdapter(songs, this@AlbumSongsActivity)
                    progressBar.visibility = View.GONE

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

}