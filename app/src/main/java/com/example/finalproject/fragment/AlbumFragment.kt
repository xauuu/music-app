package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.AlbumAdapter
import com.example.finalproject.model.Album
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Music
import com.github.ybq.android.spinkit.style.Wave
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
    lateinit var album: ArrayList<Album>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)
        recyclerView = view.findViewById(R.id.rvAlbum)
        progressBar = view.findViewById(R.id.progressBar)
        val wave = Wave()
        wave.color = resources.getColor(R.color.colorAccent)
        progressBar.indeterminateDrawable = wave
        recyclerView.layoutManager = GridLayoutManager(context, 2)
//        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val service = ApiAdapter.makeRetrofitService
        val call = service.getAlbums()
        call.enqueue(object : Callback<ArrayList<Album>> {
            override fun onResponse(
                call: Call<ArrayList<Album>>,
                response: Response<ArrayList<Album>>
            ) {
                album = response.body()!!
                recyclerView.adapter = AlbumAdapter(album, requireContext())
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<Album>>, t: Throwable) {
                t.message?.let { Log.e("ERROR LOAD ALBUM", it) }
            }

        })
        return view
    }

}