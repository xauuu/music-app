package com.example.finalproject.activity

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import com.example.finalproject.R
import com.example.finalproject.model.Song

class FavoriteActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var noFavoriteGroup: Group
    lateinit var favoriteGroup: Group

    lateinit var songs: ArrayList<Song>

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

        favoriteGroup.visibility = View.GONE
        noFavoriteGroup.visibility = View.VISIBLE

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