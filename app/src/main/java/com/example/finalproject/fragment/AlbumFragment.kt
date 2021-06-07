package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.AlbumAdapter
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.model.Album
import com.example.finalproject.api.ApiAdapter
import com.github.ybq.android.spinkit.style.FadingCircle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var albums: ArrayList<Album>
    lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        recyclerView = view.findViewById(R.id.rvAlbum)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val responseAlbum = service.getAlbums()
                withContext(context = Dispatchers.Main) {
                    albums = responseAlbum.body()!!
                    recyclerView.adapter = AlbumAdapter(albums, requireContext())

                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }
        }
        return view
    }
}