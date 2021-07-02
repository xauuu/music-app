package com.example.finalproject.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.FavoriteAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Song
import kotlinx.coroutines.*
import kotlin.random.Random

class FavoriteActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var noFavoriteGroup: Group
    lateinit var favoriteGroup: Group

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var songs: ArrayList<Song>
    lateinit var btRandom: Button
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        noFavoriteGroup = findViewById(R.id.noFGroup)
        favoriteGroup = findViewById(R.id.favoriteGroup)

        progressBar = findViewById(R.id.progressBar2)

        recyclerView = findViewById(R.id.playlistRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btRandom = findViewById(R.id.btRandom)

        sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("check", false)) {
            val service = ApiAdapter.makeRetrofitService
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.allFavorite(sharedPref.getInt("id", -1))
                    withContext(Dispatchers.Main) {
                        songs = response.body()!!
                        progressBar.visibility = View.GONE
                        if (songs.isEmpty()) {
                            noFavoriteGroup.visibility = View.VISIBLE
                            favoriteGroup.visibility = View.GONE
                        } else {
                            noFavoriteGroup.visibility = View.GONE
                            favoriteGroup.visibility = View.VISIBLE

                            recyclerView.adapter = FavoriteAdapter(songs, this@FavoriteActivity)
                            btRandom.setOnClickListener {
                                val intent = Intent(this@FavoriteActivity, SongActivity::class.java)
                                intent.putExtra("position", Random.nextInt(0, songs.size - 1))
                                intent.putExtra("check", 0)
                                intent.putExtra("list", songs)
                                startActivity(intent)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let { Log.e("ERROR FAVORITE", it) }
                }
            }
        } else {
            progressBar.visibility = View.GONE
            noFavoriteGroup.visibility = View.VISIBLE
            favoriteGroup.visibility = View.GONE
        }

    }

    private fun getStatusBarHeight(): Int {
        val height: Int
        val myResources: Resources = resources
        val idStatusBarHeight: Int = myResources.getIdentifier(
            "status_bar_height", "dimen", "android")
        height = if (idStatusBarHeight > 0) {
            resources.getDimensionPixelSize(idStatusBarHeight) - 10
        } else {
            0
        }
        return height
    }
}