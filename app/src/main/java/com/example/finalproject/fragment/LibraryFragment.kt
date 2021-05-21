package com.example.finalproject.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.activity.MainActivity
import com.example.finalproject.adapter.LibraryAdapter
import com.example.finalproject.model.Music
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {

    private var REQUEST_CODE = 1
    private var musicFiles: ArrayList<Music> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    lateinit var view1: View
    lateinit var progressBar: ProgressBar
    lateinit var searchView: SearchView
    lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        view1 = inflater.inflate(R.layout.fragment_library, container, false)
        progressBar = view1.findViewById(R.id.progressBar)
        val wave = Wave()
        wave.color = resources.getColor(R.color.colorAccent)
        progressBar.indeterminateDrawable = wave
        recyclerView = view1.findViewById(R.id.rvListLb)
        recyclerView.layoutManager = LinearLayoutManager(view1.context)
        if (checkPermission()) {
            loadSong()
            progressBar.visibility = View.GONE
        }

        searchView = view1.findViewById(R.id.search)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

        return view1
    }


    private fun loadSong() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(context = Dispatchers.Main) {
                musicFiles = getAllAudio(requireContext())
                adapter = LibraryAdapter(musicFiles, requireContext())
                recyclerView.adapter = adapter
            }
        }

    }


    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {

        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_CODE)
                } else {
                    loadSong()
                }
            }
        }
    }

    private fun getAllAudio(context: Context): ArrayList<Music> {
        val tempAudioList: ArrayList<Music> = ArrayList()

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

                tempAudioList.add(Music(id, name, album, artist, uriPath, uriPath))
            }
        }
        return tempAudioList
    }

}

