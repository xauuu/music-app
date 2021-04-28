package com.example.finalproject.fragment

import android.Manifest
import android.Manifest.permission.*
import android.bluetooth.BluetoothGattCharacteristic
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Attendees.query
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentResolverCompat.query
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.LibraryAdapter
import com.example.finalproject.data.Music


class LibraryFragment : Fragment() {

    private var REQUEST_CODE = 1
    private var musicFiles: ArrayList<Music> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    lateinit var view1: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        view1 = inflater.inflate(R.layout.fragment_library, container, false)
        activity?.title = "Thư viện"

        recyclerView = view1.findViewById(R.id.rvListLb)
        if (checkPermission()) {
            loadSong()
        }
        return view1
    }

    private fun loadSong() {
        musicFiles = getAllAudio(requireContext())
        recyclerView = view1.findViewById(R.id.rvListLb)
        recyclerView.layoutManager = LinearLayoutManager(view1.context)
        recyclerView.adapter = LibraryAdapter(musicFiles, requireContext())
    }

    private fun checkPermission(): Boolean {
        val READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        val RECORD_AUDIO = ContextCompat.checkSelfPermission(requireContext(), RECORD_AUDIO)
        val listPermissionsNeeded: ArrayList<String> = ArrayList()
        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(READ_EXTERNAL_STORAGE)
        }
        if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            requestPermissions(listPermissionsNeeded.toTypedArray(), REQUEST_CODE);
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_CODE -> {
                for (i in permissions.indices) {
                    if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        loadSong()
                    } else {
                        requestPermissions(arrayOf(permissions[i]), REQUEST_CODE)
                    }
                    return
                }
            }
        }
    }

    fun getAllAudio(context: Context): ArrayList<Music> {
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

                tempAudioList.add(Music(id, name, album, artist, "", uriPath))
            }
        }
        return tempAudioList
    }

}

