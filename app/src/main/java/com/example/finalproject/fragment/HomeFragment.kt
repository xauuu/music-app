package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.R.color.colorAccent
import com.example.finalproject.adapter.AlbumAdapter
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Album
import com.example.finalproject.model.Music
import com.github.ybq.android.spinkit.style.Circle
import com.github.ybq.android.spinkit.style.CubeGrid
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val wave = Wave()
        wave.color = resources.getColor(colorAccent)
        progressBar.indeterminateDrawable = wave

        albumsRV = view.findViewById(R.id.albumsRV)
        albumsRV.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        val service = ApiAdapter.makeRetrofitService

        val callAlbum = service.getAlbums()
        callAlbum.enqueue(object : Callback<ArrayList<Album>> {
            override fun onResponse(call: Call<ArrayList<Album>>, response: Response<ArrayList<Album>>) {
                albums = response.body()!!
                albumsRV.adapter = AlbumAdapter(albums, requireContext())
            }

            override fun onFailure(call: Call<ArrayList<Album>>, t: Throwable) {
                t.message?.let { Log.e("ERROR LOAD ALBUM", it) }
            }

        })


        bxhRV = view.findViewById(R.id.bxhRV)
        bxhRV.layoutManager = LinearLayoutManager(view.context)

        val callSong = service.getSongs()
        callSong.enqueue(object : Callback<ArrayList<Music>> {
            override fun onResponse(call: Call<ArrayList<Music>>, response: Response<ArrayList<Music>>) {
                songs = response.body()!!
                bxhRV.adapter = SongAdapter(songs, requireContext())

                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<Music>>, t: Throwable) {
                t.message?.let { Log.e("ERROR LOAD SONG", it) }
            }

        })

//        CoroutineScope(Dispatchers.IO).launch {
//            progressBar.visibility = View.VISIBLE
//            try {
//                val response = service.getSongs()
//                val response1 = service.getAlbums()
//                withContext(context = Dispatchers.Main) {
//                    songs = response.body()!!
//                    bxhRV.adapter = SongAdapter(songs, requireContext())
//
//                    albums = response1.body()!!
//                    albumsRV.adapter = AlbumAdapter(albums, requireContext())
//                    progressBar.visibility = View.GONE
//                }
//            } catch (e: Exception) {
//                e.message?.run { Log.e("NETWORK", this) }
//            }
//        }
        more = view.findViewById(R.id.navigationIcon)
        more.setOnClickListener {
            Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.albumFragment)
        }

        return view
    }
}

