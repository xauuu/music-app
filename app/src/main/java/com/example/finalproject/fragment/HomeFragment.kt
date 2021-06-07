package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.R.color.colorAccent
import com.example.finalproject.adapter.AlbumAdapter
import com.example.finalproject.adapter.ChartAdapter
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Album
import com.example.finalproject.model.Song
import com.github.ybq.android.spinkit.style.*
import com.synnapps.carouselview.CarouselView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    lateinit var albumsRV: RecyclerView
    lateinit var bxhRV: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var songs: ArrayList<Song>
    lateinit var albums: ArrayList<Album>
    lateinit var more: ImageButton
    lateinit var layout: ConstraintLayout
    lateinit var carouselView: CarouselView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        layout = view.findViewById(R.id.layout) // tìm đến id layout
        layout.visibility = View.GONE // cho ẩn đi

        progressBar = view.findViewById(R.id.progressBar)

        val images = intArrayOf(R.drawable.beauty, R.drawable.xau)
        carouselView = view.findViewById(R.id.carouselView)
        carouselView.pageCount = images.size
        carouselView.setImageListener { position, imageView ->
            imageView.setImageResource(images[position])
        }

        albumsRV = view.findViewById(R.id.albumsRV)
        albumsRV.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        bxhRV = view.findViewById(R.id.bxhRV)
        bxhRV.layoutManager = LinearLayoutManager(view.context)

        val service = ApiAdapter.makeRetrofitService

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val responseSong = service.getSongs()
                val responseAlbum = service.getAlbums()
                withContext(context = Dispatchers.Main) {
                    albums = responseAlbum.body()!!
                    albumsRV.adapter = AlbumAdapter(albums, requireContext())

                    songs = responseSong.body()!!
                    bxhRV.adapter = ChartAdapter(songs, requireContext())

                    progressBar.visibility = View.GONE
                    layout.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }
        }
        more = view.findViewById(R.id.moreAlbums)
        more.setOnClickListener {
            Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.albumFragment)
        }

        return view
    }
}

