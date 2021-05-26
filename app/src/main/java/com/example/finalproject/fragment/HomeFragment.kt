package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.synnapps.carouselview.ImageClickListener
import com.synnapps.carouselview.ImageListener
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
//    lateinit var carouselView: CarouselView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        layout = view.findViewById(R.id.layout) // tìm đến id layout
        layout.visibility = View.GONE // cho ẩn đi

        progressBar = view.findViewById(R.id.progressBar)
        val wave = FadingCircle()
        wave.color = resources.getColor(colorAccent)
        progressBar.indeterminateDrawable = wave

//        carouselView
//        val images = intArrayOf(R.drawable.beauty, R.drawable.xau)
//        carouselView = view.findViewById(R.id.carouselView)
//        carouselView.pageCount = images.size
//        carouselView.setImageListener { position, imageView ->
//            imageView.setImageResource(images[position])
//        }

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
        callSong.enqueue(object : Callback<ArrayList<Song>> {
            override fun onResponse(call: Call<ArrayList<Song>>, response: Response<ArrayList<Song>>) {
                songs = response.body()!!
                bxhRV.adapter = ChartAdapter(songs, requireContext())

                layout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<Song>>, t: Throwable) {
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

