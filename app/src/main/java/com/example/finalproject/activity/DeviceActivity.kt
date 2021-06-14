package com.example.finalproject.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.LibraryAdapter
import com.example.finalproject.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    private var REQUEST_CODE = 1
    private var songFiles: ArrayList<Song> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var searchView: SearchView
    lateinit var adapter: LibraryAdapter
    lateinit var noDGroup: Group
    lateinit var deviceGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        noDGroup = findViewById(R.id.noDGroup)
        deviceGroup = findViewById(R.id.deviceGroup)

        recyclerView = findViewById(R.id.playlistRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (checkPermission()) {
            loadSong()
        }

        searchView = findViewById(R.id.searchV)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
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

    private fun loadSong() {
        songFiles = getAllAudio(this@DeviceActivity)
        if (songFiles.isNotEmpty()) {
            noDGroup.visibility = View.GONE
            deviceGroup.visibility = View.VISIBLE
        }
        adapter = LibraryAdapter(songFiles, this@DeviceActivity)
        recyclerView.adapter = adapter

    }


    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this@DeviceActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE)
                } else {
                    loadSong()
                }
            }
        }
    }

    private fun getAllAudio(context: Context): ArrayList<Song> {
        val tempAudioList: ArrayList<Song> = ArrayList()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA)

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            uri, projection, null, null, sortOrder, null)?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val uriColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idColumn)
                val name = cursor.getString(nameColumn)
                val album = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val uriPath = cursor.getString(uriColumn)

                tempAudioList.add(Song(id, id, name, album, artist, uriPath, uriPath))
            }
        }
        return tempAudioList
    }
}