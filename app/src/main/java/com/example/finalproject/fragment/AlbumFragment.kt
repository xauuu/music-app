package com.example.finalproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.AlbumAdapter
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.data.Album
import com.example.finalproject.data.ApiAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var album: ArrayList<Album>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_album, container, false)
        activity?.title = "Album"
        recyclerView = view.findViewById(R.id.rvAlbum)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            val response = service.getAlbums()
            withContext(context = Dispatchers.Main) {
                if (response.isSuccessful) {
                    album = response.body()!!
                    recyclerView.adapter = AlbumAdapter(album, requireContext())
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
        return view
    }

}