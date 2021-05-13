package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.AlbumAdapter
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Album
import com.example.finalproject.model.Music
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    lateinit var bxhRV: RecyclerView
    lateinit var albumsRV: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var songs: ArrayList<Music>
    lateinit var albums: ArrayList<Album>
    lateinit var more: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        progressBar = view.findViewById(R.id.progressBar)

        bxhRV = view.findViewById(R.id.bxhRV)
        bxhRV.layoutManager = LinearLayoutManager(view.context)

        albumsRV = view.findViewById(R.id.albumsRV)
        albumsRV.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val service = ApiAdapter.makeRetrofitService

        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            try {
                val response = service.getSongs()
                val response1 = service.getAlbums()
                withContext(context = Dispatchers.Main) {
                    songs = response.body()!!
                    bxhRV.adapter = SongAdapter(songs, requireContext())

                    albums = response1.body()!!
                    albumsRV.adapter = AlbumAdapter(albums, requireContext())
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }
        }
        more = view.findViewById(R.id.navigationIcon)
        more.setOnClickListener {
            Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.albumFragment)
        }

        return view
    }
}

